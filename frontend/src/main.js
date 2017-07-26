import Vue from 'vue'
import VueResource from 'vue-resource'
import VueRouter from 'vue-router'

import auth from './auth'
import App from './components/App.vue'
import Main from './components/Main.vue'
import Login from './components/Login.vue'

Vue.use(VueResource)
Vue.use(VueRouter)

Vue.http.interceptors.push((request, next) => {
  if (auth.loggedIn()) {
    request.headers.set('Authorization', auth.getToken())
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
