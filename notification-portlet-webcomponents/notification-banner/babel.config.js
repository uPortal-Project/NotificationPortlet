module.exports = function(api) {
  api.cache.never();
  return {
    plugins: ["babel-plugin-transform-custom-element-classes"],
    presets: [
      [
        "@vue/babel-preset-app",
        {
          useBuiltIns: false
        }
      ]
    ]
  };
};
