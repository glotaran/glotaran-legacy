---
applyTo: "{ASCIIDataloader/**,AVGDataloader/**,ExampleDataloader/**,LabmonkeyDataloader/**,PQDataloader/**,RawDataloader/**,SDTDataloader/**,csvdataloader/**,imgdataloader_new/**}"
---
# Dataloader Modules Guidance

These modules are format-specific adapters and should remain narrowly scoped.

## Editing rules

- Keep parser/loader logic inside the owning dataloader module.
- Do not add broad platform dependencies to a dataloader unless clearly required.
- Preserve existing input format behavior; treat compatibility regressions as high risk.
- Prefer adding targeted tests/fixtures when changing parsing logic.

## Cross-module behavior

- If loader API usage changes, verify the impact on Core/UI consumers before widening changes.
- Avoid coupling dataloaders directly to unrelated feature modules.

## Validation expectations

- Validate with root reactor verify command after loader behavior changes:

```sh
mvn -B -T 1C clean verify --file pom.xml --no-transfer-progress
```
