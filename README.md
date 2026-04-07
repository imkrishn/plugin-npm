### NPM Dependency Analyzer Plugin

A simple Kestra plugin to check outdated npm dependencies and run security audits directly inside workflows.

It wraps common npm commands like:

```bash
npm outdated            // table view    
npm outdated --json     // raw data or json view  
npm audit --json
```

and lets you choose between raw (table) or JSON output.

### Usage

tasks:
  - id: check-deps
    type: io.kestra.plugin.npm.NpmOutdated
    path: "/your/project"


**Demo :**

<img width="1360" height="626" alt="Screenshot from 2026-04-07 23-09-49" src="https://github.com/user-attachments/assets/6b1b1d41-dcf5-41e8-98db-cc650231c209" />

https://github.com/user-attachments/assets/011f759d-f4c4-4cad-9cc2-4b5f3cbd8099
