goog.provide('voting.index');

goog.require('voting.views');

goog.scope(function() {
  var idom = goog.module.get('incrementaldom');

  voting.index.main = function() {
    idom.patch(
      document.getElementById('app'),
      voting.views.voting,
      {pair: ["Trainspotting", "28 Days Later"],
       hasVoted: "Trainspotting"}
    );
  };
});
