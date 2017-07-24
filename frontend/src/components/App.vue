<template>
    <div id="app">
        <div class='container'>
            <Header/>
            <template v-if="$route.matched.length">
              <router-view></router-view>
            </template>
            <template v-else>
              <p>You are logged {{ loggedIn ? 'in' : 'out' }}</p>
            </template>
        </div>
    </div>
</template>

<script>
import Header from './Header.vue'

export default {
    name: 'app',
    data () {
        return {
        }
    },
    components: {
        Header
    }
}
</script>

<style>
</style>
