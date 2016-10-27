/*
 * "random.js" requires https://github.com/rubycon/isaac.js/blob/master/isaac.js
 * Copyright (c) 2014 Simon Massey
 * http://www.apache.org/licenses/LICENSE-2.0
 */
/*
This module tries to use window.crypto random number generator which is available 
in modern browsers. If it cannot find that then it falls back to using an isaac 
random number generator which is seeded by Math.random and any cookies. To improve
security whcn using isaac it will skip forward until some time has passed. This will
make the amount of randoms skipped determined by hardware/browser/load. You can attach
the skip method to html input boxes with:
random16byteHex.advance(Math.floor(event.keyCode/4));
which will further advance the stream an unpredictable amount. If the browser
has built in crypto randoms the advance method with do nothing.
Do not add to the password box to leak any info about the password to the outside world.
*/
var random16byteHex = (function() {

  function isWebCryptoAPI() {
    if (typeof(window) != 'undefined' && window.crypto && window.crypto.getRandomValues) {
      return true;
    }
    else if (typeof(window) != 'undefined' && window.msCrypto && window.msCrypto.getRandomValues) {
      return true;
    } else {
      return false;
    }
  };

  var crypto = isWebCryptoAPI();

  function seedIsaac() {
    //console.log("isWebCryptoAPI:"+crypto);
    if( crypto ) return false;
    var value = +(new Date())+':'+Math.random();
    if( typeof(window) != 'undefined' && window.cookie) {
      value += document.cookie;
    }
    var h = CryptoJS.SHA256 ||  CryptoJS.SHA1;
    isaac.seed(h(value));
    return true;
  }

  var seeded = seedIsaac();

  function random() {
    var wordCount = 4;
    var randomWords;

    if( crypto ) {
      var acrypto = window.crypto || window.msCrypto;
      randomWords = new Int32Array(wordCount);
      acrypto.getRandomValues(randomWords);
    } else {
        // skip forward an unpredictable amount
        var now = +(new Date());
        var t = now % 50;
        isaac.prng(1+t);

        // grab some words
        randomWords = new Array();
        for (var i = 0; i < wordCount; i++) {
            randomWords.push(isaac.rand());
        }
    }

    var string = '';
    
    for( var i=0; i<wordCount; i++ ) {
      var int32 = randomWords[i];
      if( int32 < 0 ) int32 = -1 * int32;
      string = string + int32.toString(16);
    }
    //console.log(string);
    return string;
  };
  	

  /**
  Run this within onkeyup of html inputs so that user typing makes the random numbers more random:
  random16byteHex.advance(Math.floor(event.keyCode/4));
  */
  function advance(ms) {
    if( !crypto ) {
      var start = +(new Date());
      var end = start + ms;
      var now = +(new Date());
      while( now < end ) {
          var t = now % 5;
          isaac.prng(1+t);
          now = +(new Date());
      }
    }
  }
  
  return {
    'random' : random,
    'isWebCryptoAPI' : crypto,
    'advance' : advance 
  };
})();

// if using isaac in a browser without secure random numbers spend 0.1s advancing the random stream
var random16byteHexAdvance = 100;

// optional override during unit tests
if( typeof test_random16byteHexAdvance != 'undefined' ) {
	random16byteHexAdvance = test_random16byteHexAdvance;
}

random16byteHex.advance(random16byteHexAdvance);
