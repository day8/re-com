# Re-com Release Procedure

## Setting your environment up

A number of things need to be set up before the release procedure can commence.


### GPG

NOTE: GPG signing is no longer required but we'll leave the process here just in case we need to reinstate it...

Here are the basic instructions to follow to get GPG running: https://github.com/technomancy/leiningen/blob/stable/doc/GPG.md

A few notes for Windows:

 - The recommended gpg version for windows is: http://www.gpg4win.org.
 - There are several version on the download page. I used the "Vanilla" one. Direct link: http://files.gpg4win.org/gpg4win-vanilla-2.2.4.exe
 - To display your public key, just type: `gpg --export -a`.


### Clojars

Simply need to set up your own account, then get the Day8 Clojars admin to add you to the family. Then you can publish to Day8 Clojars.
 
Note that your GPG public key from above needs to be pasted into your Clojars profile for this process to work.

More info on deploying libraries using lein: https://github.com/technomancy/leiningen/blob/master/doc/DEPLOY.md


## Release Steps

### Build library and test the demo

Note that all these commands are entered at the repo root folder.

- [ ] Close all auto-compiles (command line and/or IntelliJ).
- [ ] Build each of these aliases (will require separate terminals for each):

       lein dev-auto
       lein prod-auto
       lein test-auto

- [ ] For `dev` and `prod`, run through each demo page and make sure no errors or debug output appears in the console. 
- [ ] For `test`, make sure all tests pass. Modify code/tests until all tests pass. 
- [ ] Close all auto-compiles again.


### Make a Github release

- [ ] Finish any feature branch you're working on. You should now be on the master branch.
- [ ] Bump version in project.clj to `x.x.x`.
- [ ] Update README.md file if required.
- [ ] Push master:

       git commit -a -m "Bumped version to x.x.x  etc."
       git tag x.x.x
       git push
       git push --tags

- [ ] Create a GitHub Release:
       - Go to: https://github.com/Day8/re-com/releases
       - Should see your version `x.x.x` tag at the top.
       - Press the `Draft a new release` button.
       - Select this new `x.x.x` version in the Tag version dropdown.
       - For the title, enter the version number: `x.x.x`.
       - Enter a description that includes a list of "Changes" and "Fixes" (since the last release).
       - Click the `Publish release` button.


### Push library to Clojars

- [ ] Push this release to Clojars:

       lein deploy
       ---
       Will prompt for your Clojars username and password
       Note: `project.clj` has been modified to no longer require signing, so it will no longer prompt for a passphrase


### Deploy demo to AWS

- [ ] Deploy demo to AWS:

       lein s3-static-deploy
       ---
       Could have used `lein deploy-aws` but this also builds the `prod` version which we have already just built.
       Manually change `index.html` to `index_prod.html` in S3 Browser. TODO: Find a way to automate this.
       Test it: https://re-com.day8.com.au.


### Final tasks

- [ ] Reply to all issues and pull requests relating to this release.
