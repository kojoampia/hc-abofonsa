// Shows the "still loading" notice if the bundle has not booted after four seconds.
//
// An external file rather than the inline <script> generator-jhipster puts at the end of
// index.html, because the Content-Security-Policy is `script-src 'self'` with no 'unsafe-inline'
// and an inline script is refused. It was refused silently — the page still worked, so nothing
// looked wrong; only the slow-connection notice stopped appearing, on exactly the connections
// nobody testing this is using.
//
// Registered in angular.json under `scripts`, which copies it to the output and adds a plain
// <script src> tag for it.
(function () {
  function showError() {
    var errorElm = document.getElementById('jhipster-error');
    if (errorElm && errorElm.style) {
      errorElm.style.display = 'block';
    }
  }

  window.addEventListener('load', function () {
    setTimeout(showError, 4000);
  });
})();
