---
applyTo: "{application/**,branding/**}"
---
# Application Packaging Guidance

Applies to NetBeans application assembly and branding.

## Run and packaging flow

- Runnable module is `application/`.
- Maven run command from `application/`:

```sh
mvn nbm:cluster-app nbm:run-platform
```

- Built Windows launcher output:
  - `application/target/glotaran/bin/`
  - `application/target/glotaran/bin/glotaran64.exe`

## Windows environment guardrails

- Ensure `JAVA_HOME` points to JDK root, not `...\\bin`.
- Avoid trailing backslash in `JAVA_HOME`.
- If launcher config reports invalid `jdkhome`, verify environment first before repo edits.

## Editing rules

- Keep NetBeans application packaging aligned with this branch's setup, described in [RELEASING.md](../../RELEASING.md).
- `application/src/main/resources/glotaran.conf` must keep LF line endings; it is sourced by `/bin/sh` on Linux and macOS.
- Do not introduce packaging conventions from other branches without explicit request.
