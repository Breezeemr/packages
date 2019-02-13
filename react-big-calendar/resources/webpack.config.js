var webpack = require("webpack");
var isProduction = process.argv.indexOf('--production') !== -1;
var entryPath = "./main.js";
var entryName = "react-big-calendar";
var output = {
  filename: '[name].inc.js'
};
var externals = {
  "react": "React",
  "react-dom": "ReactDOM"
};

var entry = {};
if (isProduction) {
  entryName = entryName + ".min";
}
entry[entryName] = entryPath;

module.exports = {
  entry : entry,
  output: output,
  externals: externals,
  plugins: [
    new webpack.DefinePlugin({
      'process.env': {
        'NODE_ENV': isProduction ? '"production"' : '"development"'
      }
    }),
    new webpack.optimize.UglifyJsPlugin({
      include: /\.min\.inc\.js$/,
      minimize: true
    })
  ]
};
