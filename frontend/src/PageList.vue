<template>
    <div class='panel panel-default'>
        <div class='panel-header'>
            <h3>{{ filter }}</h3>
        </div>
        <table class='table table-striped'>
            <tbody>
                <tr v-for="page in pages">
                    <td>{{ page.name }}</td>
                    <td><input type='checkbox' v-model="page.active" v-on:click="setPageStatus(page.name, page.active)" /></td>
                </tr>
            </tbody>
        </table>
    </div>
</template>

<script>
import BootstrapToggle from 'vue-bootstrap-toggle'
export default {
    components: { BootstrapToggle },
    name: 'header',
    props: ['filter'],
    data () {
        return {
            pages: []
        }
    },
    methods: {
        setPageStatus(name, state) {
            if (state == true) {
                this.$http.delete('/app/pages/' + name)
                    .then(function(response) {
                        this.updatePage(name, response.body.active)
                    })
            } else {
                this.$http.put('/app/pages/' + name)
                    .then(function(response) {
                        this.updatePage(name, response.body.active)
                    })
            }
        },
        updatePage(name, active) {
            for (var i = 0; i < this.pages.length; i++) {
                var page = this.pages[i];
                if (page.name == name) {
                    page.active = active
                }
            }
        },
        fetchPages() {
            this.$http.get('/app/pages')
            .then(function(response) {
                this.pages = this.filterBy(response.body.pages, this.filter)
            })
        },
        filterBy(list, value){
            value = value.charAt(0).toUpperCase() + value.slice(1);
            return list.filter(function(page){
                return page.name.startsWith(value)
            });
        }
    },
    created() {
        this.fetchPages()
    }
}
</script>

<style>
.panel {
    border: 0;
}
.table > tbody > tr > td {
    vertical-align: middle;
    border: 0;
}
.table > tbody > tr > td:last-of-type {
    width: 100px;
}
</style>
