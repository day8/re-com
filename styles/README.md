## Building bootstrap.css
 
Reagent components use two style libraries

 * bootstrap
 * bootstrap-chosen
 
This procedure will combine both libraries into a single css file and provide two versions of it: bootstrap.css and bootstrap.min.css.

Both bootstrap and bootstrap-chosen are provided as one or more less files. [Click here for info on less](http://lesscss.org).
 
Bootstrap-chosen depends on Bootstrap less variables and this is why it's important to combine them in this way.   
 
### Requirements

You need to have a less compiler at the ready. We use the standard lessc which is a Node.js application.

To to set up lessc from scratch:

 * Install Node.js from [here](http://nodejs.org/download). This will provide access to the `npm` command. 
 * Enter the `npm install -g less` command. This will provide access to the `lessc` command.
 
OK, we're ready to compile, but we need to retrieve the appropriate less files first.

### Retrieving the less files


#### Bootstrap

This version of re-com uses bootstrap version 3.2.0:

 * Go to the home page for this version: [Bootstrap v3.2.0](https://github.com/twbs/bootstrap/releases/tag/v3.2.0)
 * Scroll to the bottom and click the button to download the source zip file (the middle one). 
 * Extract the contents of the `/bootstrap-3.2.0/less` folder in the zip file to the `/styles/bootstrap/less` folder in the re-com root folder.
 * So that the glyph icons also work, extract the contents of the `/bootstrap-3.2.0/fonts` folder in the zip file into the `/styles/dist/fonts` folder in the re-com root folder.
 
### Bootstrap-chosen

Bootstrap-chosen doesn't have releases but at time of writing, the commit number was 45:

 * Go to the home page for bootstrap-chosen: [Bootstrap-chosen](https://github.com/alxlit/bootstrap-chosen)
 * Download `bootstrap-chosen.less` and `bootstrap-chosen-variables.less` into the `/styles/bootstrap-chosen` folder in the re-com root folder. 
 * Download `chosen-sprite.png` and `chosen-sprite@2x.png` into the `/styles/dist/css` folder in the re-com root folder. 
 
### Compiling

We have to link the two libraries, which requires editing the bootstrap master less file: `/styles/bootstrap/bootstrap.less`.
Simply add the following line to the end of the file to make sure it includes the bootstrap-chosen CSS:

    @import "../../bootstrap-chosen/bootstrap-chosen.less";

From the `/styles` folder, enter the following commands to create the final files, ready for distribution:

    lessc ./bootstrap/bootstrap.less > ./styles/dist/css/bootstrap.css 
    lessc -x ./bootstrap/bootstrap.less > ./styles/dist/css/bootstrap.min.css

### Upload to the Day8 CDN

TODO: Currently use [S3 Browser](http://s3browser.com) to upload to `static.day8.com.au/re-com` folder.

### Adding the css file to your app

Simply add one of the following lines to the &lt;head&gt; section of your app:
 
    <link type="text/css" rel="stylesheet" href="http://static.day8.com.au/re-com/0.1.0/css/bootstrap.css">
    <link type="text/css" rel="stylesheet" href="http://static.day8.com.au/re-com/0.1.0/css/bootstrap.min.css">
