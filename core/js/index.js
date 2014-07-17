// Debug helper function
window.prstr = function (obj) {
  return cljs.core.pr_str(obj)
}

window.onload = function () {
  try {
    reagent_components.core.init();
  } catch (e) {
    console.error(e);
    console.log(e.stack);
  }

  //give it a little bit to paint before loading.
  // setTimeout(function () {
  //   var script = document.createElement("script");
  //   script.type = "text/javascript";
  //   script.async = false;
  //   script.src = "out/reagent_components.js";
  //   document.body.appendChild(script);
  //   script.onload = function () {
  //     try {
  //       reagent_components.core.init();
  //     } catch (e) {
  //       console.error(e);
  //       console.log(e.stack);
  //     }
  //   }
  // }, 40);
}
