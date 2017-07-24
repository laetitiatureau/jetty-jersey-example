<template>
  <div>
  <h3>{{ filter }}</h3>
  <div>
    <div v-for="page in pages">
      <div class="well">
      <div>
        <span class="page-title">{{ page.name }}</span>
        <span class="page-toggle">
        <bootstrap-toggle v-model="page.active" :options="{ on: 'On', off: 'Off' }"/>
        </span>
      </div>
    </div>
    </div>
  </div>
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
                this.$http.delete('http://localhost:8080/api/pages/' + name)
                    .then(function(response) {
                        this.updatePage(name, response.body.active)
                    })
            } else {
                this.$http.put('http://localhost:8080/api/pages/' + name)
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
            this.$http.get('http://localhost:8080/api/pages')
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
.page-title {
  vertical-align: bottom;

}
.page-toggle {
  float:right;
  vertical-align: middle;
}
</style>
