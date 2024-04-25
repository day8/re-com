module.exports = function (config) {
    var root = './target/karma' // same as :output-dir
    var junitOutputDir = "target/karma/junit"

    config.set({
        frameworks: ['cljs-test'],
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
        customLaunchers: {
            ChromeHeadlessNoSandbox: {
                base: 'ChromeHeadless',
                flags: ['--no-sandbox', '--disable-setuid-sandbox']
            }
        },
        browsers: ['ChromeHeadlessNoSandbox'],

        // the default configuration
        junitReporter: {
            outputDir: junitOutputDir + '/karma', // results will be saved as outputDir/browserName.xml
            outputFile: undefined, // if included, results will be saved as outputDir/browserName/outputFile
            suite: '' // suite will become the package name attribute in xml testsuite element
        }
    })
}
