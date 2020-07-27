module.exports = function (config) {
  var root = './target/karma' // same as :output-dir
  var junitOutputDir = "target/karma/junit"
  var browsers = ['ChromeHeadless']

  config.set({
    frameworks: ['cljs-test'],
    browsers: browsers,
    basePath: root,
    files: [
      'test.js'
    ],
    plugins: [
        'karma-cljs-test',
        'karma-chrome-launcher',
        'karma-junit-reporter'
    ],
    colors: true,
    logLevel: config.LOG_INFO,
    client: {
      args: ['shadow.test.karma.init'],
      singleRun: true
    },

    // the default configuration
    junitReporter: {
      outputDir: junitOutputDir + '/karma', // results will be saved as outputDir/browserName.xml
      outputFile: undefined, // if included, results will be saved as outputDir/browserName/outputFile
      suite: '' // suite will become the package name attribute in xml testsuite element
    }
  })
}
