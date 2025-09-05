# Re-com Release Procedure

## Release Steps

### Build library and test the demo

Note that all these commands are entered at the repo root folder.

- [ ] Finish any feature branch you're working on. You should now be on the master branch.
- [ ] Update README.md file if required and commit it.
- [ ] Close all auto-compiles (command line and/or IntelliJ).
- [ ] Run each of these tasks (will require separate terminals for each):

      bb watch
      bb release-demo
      bb ci

- [ ] For `dev` and `prod`, run through each demo page and make sure no errors or debug output appears in the console. 
- [ ] For `test`, make sure all tests pass. Modify code/tests until all tests pass. 
- [ ] Close all auto-compiles again.

### Tag release 

- [ ] Tag the release on GitHub:

      git tag v2.10.0 HEAD
      git push --tags

NOTE: Tagging the release will trigger GitHub Actions to deploy the library to Clojars and the demo site to AWS S3.

### Final tasks

- [ ] Reply to all issues and pull requests relating to this release.
