# Re-com Release Procedure

## Setting your environment up

A number of things need to be set up before the release procedure can commence.

### GPG

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
       - `lein debug`
       - `lein debug-prod`
       - `lein debug-test`
- [ ] For `dev` and `prod`, run through each demo page and make sure no errors or debug output appears in the console. 
- [ ] For `test`, make sure all tests pass. Modify code/tests until all tests pass. 
- [ ] Close all auto-compiles again.

### Make a Github release

- [ ] TODO: IntelliJ should be able to do all of these steps as it has integrated gitflow - investigate.
- [ ] Finish any feature branch you're working on. You should now be in the develop branch.
- [ ] If you haven't commited/pushed develop, do so now.
- [ ] Make sure your master branch is up-to-date with Github (in 99% of cases it will be):
       - `git checkout master`
       - `git pull`
       - `git checkout develop`
- [ ] Start a new release:
       - `git flow release start x.x.x`
       - A branch called 'release/x.x.x' is created.
       - This branch is checked out.
- [ ] Bump version in project.clj to `x.x.x`.
- [ ] Update README.md file if required.

---------------------------------------------------------------------------
*** TODO: LINES TAGGED FOR REMOVAL - NOT REQUIRED - TO BE TESTED ***
- [ ] Commit this branch:
       - `git commit -a -m "Bumped version to x.x.x  etc."` 
- [ ] Push this branch GitHub:
       - `git push` ==> `git push --set-upstream origin release/0.5.4`
---------------------------------------------------------------------------

- [ ] Finish the release:
       - `git flow release finish 'x.x.x'`
       - Pops up an editor to describe the release. I just put "Version x.x.x".
       - Merges release/x.x.x into master, then back merges into develop.
       - Creates a tag with that version number.
       - Deletes the release branch.
       - Now you're back on the develop branch.
- [ ] Push develop to GitHub:
       - `git push`
- [ ] Go to master and push that (and the tags):
       - `git checkout master`
       - `git push`
       - `git push --tags`
- [ ] Create a GitHub Release:
       - Go to: https://github.com/Day8/re-com/releases
       - Should see your version `x.x.x` tag at the top.
       - Press the `Draft a new release` button.
       - Select this new `x.x.x` version in the Tag version dropdown.
       - Make sure this is based on a Target of master.
       - For the title, enter the version number: `x.x.x`.
       - Enter a description that includes a list of "Changes" and "Fixes" (since the last release).
       - Click the `Publish release` button.

### Push library to Clojars

- [ ] Push this release to Clojars:
       - `lein deploy clojars`
       - Will prompt for your Clojars username and password.
       - Will also prompt for your GPG passphrase.
- [ ] "Promote" the Clojars release:
       - Go to the re-com Clojars page for this version: https://clojars.org/re-com
       - Log in.
       - Click on the link for this new version.
       - Click the big blue button at the bottom of the release page to make it live.

### Deploy demo to AWS

- [ ] Deploy demo to AWS:
       - `lein s3-static-deploy`
       - Could have used `lein deploy-aws` but this also builds the `prod` version which we have already just built.
       - Manually change `index.html` to `index_prod.html` in S3 Browser. TODO: Find a way to automate this.
       - Test it: http://re-demo.s3-website-ap-southeast-2.amazonaws.com.

### Final tasks

- [ ] Reply to all issues and pull requests relating to this release.
- [ ] Post a note in #re-com Slack channel pointing to GitHub release page.
- [ ] Post to Google Groups if necessary.
