<template>
    <div id="app">
        <div class='container'>
            <Header title='Maintenance Pages'/>
            <div class="row">
                <div class="col-sm-4">
                    <PageList filter="Env1"/>
                </div>
                <div class="col-sm-4">
                    <PageList filter="Env2"/>
                </div>
                <div class="col-sm-4">
                    <PageList filter="Env3"/>
                </div>
            </div>
        </div>
    </div>
</template>

<script>
import Header from './Header.vue'
import PageList from './PageList.vue'

export default {
    name: 'app',
    data () {
        return {
            msg: 'Welcome to Your Vue.js App'
        }
    },
    components: {
        Header, PageList
    }
}
</script>

<style>
</style>
