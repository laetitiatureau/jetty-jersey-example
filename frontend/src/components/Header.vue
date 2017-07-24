<template>
    <nav class="navbar navbar-default" v-if="loggedIn">
  <div class="container-fluid">
    <ul class="nav navbar-nav navbar-right">
      <li>
        <router-link to="/logout">Log out</router-link>
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
