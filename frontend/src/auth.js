/* globals localStorage */

export default {
  login (context, email, pass, cb) {
    cb = arguments[arguments.length - 1]
    if (localStorage.token) {
      if (cb) cb(true)
      this.onChange(true)
      return
    }
    sendAuthRequest(context, email, pass, (res) => {
      if (res.authenticated) {
        localStorage.token = res.token
        if (cb) cb(true)
        this.onChange(true)
      } else {
        if (cb) cb(false)
        this.onChange(false)
      }
    })
  },

  getToken () {
    return localStorage.token
  },

  logout (cb) {
    delete localStorage.token
    if (cb) cb()
    this.onChange(false)
  },

  loggedIn () {
    return !!localStorage.token
  },

  onChange () {}
}

function sendAuthRequest (context, email, pass, cb) {
  setTimeout(() => {
    var data = { 'username': email, 'password': pass }
    context.$http.post('http://localhost:8080/api/users', data, { emulateJSON: true }).then(response => {
      cb({
        authenticated: true,
        token: response.body.token
      })
    }, response => {
      cb({
        authenticated: false
      })
    });
  }, 0)
}
