<template>
    <nav class="navbar navbar-default" v-if="loggedIn">
  <div class="container-fluid">
    <ul class="nav navbar-nav">
      <li>
        <router-link to="/">Home</router-link>
      </li>
      <li>
        <router-link v-if="loggedIn" to="/logout">Log out</router-link>
      </li>
    </ul>
  </div>
</nav>
</template>

<script>
import auth from '../auth'
export default {
  name: 'header',
  data () {
    return {
       loggedIn: auth.loggedIn()
    }
  },
  created () {
    auth.onChange = loggedIn => {
      this.loggedIn = loggedIn
    }
  }
}
</script>

<style>
</style>
