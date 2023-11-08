## Commits convention
Commit types

- 📃: Documentation (Involves any changes to the documentation, including improve docstrings in codebase, but excluding comments lines)
- 🌟: Feature (New functionality or significant changes that modify the way the app works)
- 🧩: Codebase (Changes that involve existing feature not related to bug fixes)
- 🐞: Bug fix (Error corrections and bug fixes thats not involve new functionality)
- 🎨: Style (Changes in the codebase that do not affect the meaning of the code, such as white-space, tabs, formatting, comments)
- 🔁: Refactor (Any code change that does not fix a bug or add a feature but improves the codebase readability, such as renaming, code reorganization, optimization) 
- 🔧: Config (Operations related to configuration or environment files that do not involve code changes)
- 📦: Project (Changes in the project structure, such as folders, files, dependencies, build system)

Commit scopes

- 🧐: Testing
- 🔮: Frontend
- 📡: Backend
- 🗄️: Database

Commit structure

```
<type>(<[optional]scope>|...|<[optional]scope>): <subject>
```

Example commit

```
📃: Add commit conventions to README.md
🐞(🧩): Fix bug in the codebase
🔁(🧩|📦): Use main to execute script and be able to run inbuild inteliJ with deps
```
