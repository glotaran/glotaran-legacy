#!/usr/bin/env bash
# Starts an installed Glotaran and checks its messages.log:
# - the main window came up (the NetBeans warm-up tasks ran),
# - the bundled runtime under INSTALL_DIR was used,
# - every Glotaran module that is enabled by default was turned on,
# - nothing was logged at SEVERE level.
#
# Usage: smoke-test.sh INSTALL_DIR COMMAND [ARGS...]
#   INSTALL_DIR  installation root; contains glotaran/config/Modules and the runtime
#   COMMAND      launcher as users start it, for example /usr/bin/glotaran
set -uo pipefail

INSTALL_DIR="$1"
shift
TIMEOUT=${SMOKE_TIMEOUT:-300}

UD="${RUNNER_TEMP:-/tmp}/glotaran-smoke-userdir"
rm -rf "$UD"
mkdir -p "$UD"
if command -v cygpath > /dev/null; then UD=$(cygpath -m "$UD"); fi
LOG="$UD/var/log/messages.log"

"$@" --nosplash --userdir "$UD" > "$UD/stdout.txt" 2>&1 &
PID=$!

stop_app() {
  if [ -r "/proc/$PID/winpid" ]; then
    # Git Bash on Windows: kill the launcher and the JVM process it started
    taskkill //F //T //PID "$(cat "/proc/$PID/winpid")" > /dev/null 2>&1
  else
    pkill -f "glotaran-smoke-userdir" > /dev/null 2>&1
  fi
  kill "$PID" > /dev/null 2>&1
  wait "$PID" 2> /dev/null
}

started=false
for ((i = 0; i < TIMEOUT; i += 5)); do
  if [ -f "$LOG" ] && grep -q "org.netbeans.core.ui.warmup.DiagnosticTask" "$LOG"; then
    started=true
    break
  fi
  if ! kill -0 "$PID" 2> /dev/null; then
    echo "::error::Glotaran exited before the main window was shown"
    break
  fi
  sleep 5
done
# Let module installers and the update check finish logging
$started && sleep 10
stop_app

echo "----- launcher output -----"
cat "$UD/stdout.txt"
echo "----- messages.log -----"
cat "$LOG" 2> /dev/null || echo "(no messages.log)"
echo "------------------------"

failed=false
if ! $started; then
  echo "::error::Main window not shown within ${TIMEOUT}s"
  failed=true
fi

norm() { tr '\\' '/' | tr '[:upper:]' '[:lower:]' | sed 's#/*$##'; }
java_home=$(grep -m1 -E '^[[:space:]]+Java Home[[:space:]]+=' "$LOG" 2> /dev/null | sed -E 's/^[^=]*= //' | tr -d '\r')
expected_root=$(printf '%s' "$INSTALL_DIR" | norm)
echo "Java Home: $java_home"
case "$(printf '%s' "$java_home" | norm)" in
  "$expected_root"/*) ;;
  *)
    echo "::error::Java Home '$java_home' is not the runtime bundled under '$INSTALL_DIR'"
    failed=true
    ;;
esac

expected_modules=$(grep -l '"enabled">true' "$INSTALL_DIR"/glotaran/config/Modules/org-glotaran-*.xml | wc -l)
enabled_modules=$(grep -c -E '^[[:space:]]+org\.glotaran\.' "$LOG" 2> /dev/null)
echo "Glotaran modules turned on: ${enabled_modules:-0} of $expected_modules"
if [ "$expected_modules" -eq 0 ] || [ "${enabled_modules:-0}" -ne "$expected_modules" ]; then
  echo "::error::Expected $expected_modules Glotaran modules to be turned on, found ${enabled_modules:-0}"
  failed=true
fi

if grep -q '^SEVERE' "$LOG" 2> /dev/null; then
  echo "::error::messages.log contains SEVERE entries"
  failed=true
fi

if $failed; then exit 1; fi
echo "Smoke test passed"
