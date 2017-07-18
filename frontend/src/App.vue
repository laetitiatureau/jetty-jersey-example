<template>
    <div id="app">
        <div class='container'>
            <Header title='Maintenance Pages'/>
            <div class="row">
                <div class="col-sm-4">
                    <PageList filter="SIT"/>
                </div>
                <div class="col-sm-4">
                    <PageList filter="PREPROD"/>
                </div>
                <div class="col-sm-4">
                    <PageList filter="PROD"/>
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
