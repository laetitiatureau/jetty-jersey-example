import Vue from 'vue'
import VueResource from 'vue-resource'
import VueRouter from 'vue-router'
import Notifications from 'vue-notification'

import Alert from './components/Alert.vue'
import App from './components/App.vue'
import Login from './components/Login.vue'
import Main from './components/Main.vue'

import auth from './auth'

Vue.use(VueResource)
Vue.use(VueRouter)
Vue.use(Notifications)

Vue.http.interceptors.push((request, next) => {
  if (auth.loggedIn()) {
    request.headers.set('Authorization', 'Bearer ' + auth.getToken())
  }
  next()
})

function requireAuth (to, from, next) {
  if (!auth.loggedIn()) {
    next({
      path: '/login',
      query: { redirect: to.fullPath }
    })
  } else {
    next()
  }
}

const router = new VueRouter({
  mode: 'history',
  base: __dirname,
  routes: [
    { path: '/', component: Main, beforeEnter: requireAuth },
    { path: '/login', component: Login },
    { path: '/logout', beforeEnter (to, from, next) {
      auth.logout()
      next('/login')
    } }
  ]
})

new Vue({
  el: '#app',
  router,
  render: h => h(App)
})
