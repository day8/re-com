# Re-com Release Procedure

## Setting your environment up

A number of things need to be set up before the release procedure can commence.


### GPG

```
NOTE: GPG signing is no longer required but we'll leave the process here just in case we need to reinstate it...
```

Here are the basic instructions to follow to get GPG running: https://github.com/technomancy/leiningen/blob/stable/doc/GPG.md

A few notes for Windows:

 - The recommended gpg version for windows is: http://www.gpg4win.org.
 - There are several version on the download page. I used the "Vanilla" one. Direct link: http://files.gpg4win.org/gpg4win-vanilla-2.2.4.exe
 - To display your public key, just type: `gpg --export -a`.


### Clojars

Simply need to set up your own account, then get the Day8 Clojars admin to add you to the family. Then you can publish to Day8 Clojars.
 
Note: If using GPG, your GPG public key from above needs to be pasted into your Clojars profile for this process to work.

More info on deploying libraries using lein: https://github.com/technomancy/leiningen/blob/master/doc/DEPLOY.md

You should set up your Clojars authentication in your Leiningen profile: `~/.lein/profiles.clj` (or `%USERPROFILE%\.lein\profiles.clj` for Windows):
```
{:user {}
 :auth {
  :repository-auth {
    #"https://clojars.org/repo" {:username "your-username" :password "your-password"}}}}
```


## Release Steps

### Build library and test the demo

Note that all these commands are entered at the repo root folder.

- [ ] Finish any feature branch you're working on. You should now be on the master branch.
- [ ] Update README.md file if required and commit it.
- [ ] Close all auto-compiles (command line and/or IntelliJ).
- [ ] Build each of these aliases (will require separate terminals for each):

      lein dev-auto
      lein prod-auto
      lein test-auto

- [ ] For `dev` and `prod`, run through each demo page and make sure no errors or debug output appears in the console. 
- [ ] For `test`, make sure all tests pass. Modify code/tests until all tests pass. 
- [ ] Close all auto-compiles again.


### Push library to Clojars

- [ ] Push this release to Clojars:

      lein release :minor


### Deploy demo to AWS

- [ ] Deploy demo to AWS:

      lein deploy-aws

Test it: https://re-com.day8.com.au.

If it can't find the site, you may need to change `index.html` to `index_prod.html` in S3 Browser (although this was unnecessary with the most recent build).


### Make a Github release

- [ ] Create a GitHub Release:

    - Go to: https://github.com/Day8/re-com/releases
    - Should see your version `x.x.x` tag at the top.
    - Press the `Draft a new release` button.
    - Select this new `x.x.x` version in the Tag version dropdown.
    - For the title, enter the version number: `x.x.x`.
    - Enter a description that includes a list of "Changes" and "Fixes" (since the last release).
    - Click the `Publish release` button.


### Final tasks

- [ ] Reply to all issues and pull requests relating to this release.
