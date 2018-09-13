module.exports = function(api) {
  api.cache.never();
  return {
    plugins: [
      "babel-plugin-transform-custom-element-classes",
      "@babel/plugin-transform-arrow-functions"
    ],
    presets: ["@babel/preset-env", "babel-preset-minify", "@vue/app"]
  };
};
