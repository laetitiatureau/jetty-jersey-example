<template>
  <div class="row">
    <notifications position="bottom right"/>
    <h2 class="text-center">Maintenance Pages</h2>
    <div class="col-xs-12 col-sm-8 col-md-6 col-sm-offset-2 col-md-offset-3">
      <PageList title="Env4" :pages="empty_pages"/>
    </div>
    <div class="col-xs-12 col-sm-8 col-md-6 col-sm-offset-2 col-md-offset-3">
      <PageList title="Env1" :pages="env1_pages"/>
    </div>
    <div class="col-xs-12 col-sm-8 col-md-6 col-sm-offset-2 col-md-offset-3">
      <PageList title="Env2" :pages="env2_pages"/>
    </div>
    <div class="col-xs-12 col-sm-8 col-md-6 col-sm-offset-2 col-md-offset-3">
      <PageList title="Env3" :pages="env3_pages"/>
    </div>
    <div class="col-xs-12 col-sm-4 col-md-2 col-sm-offset-4 col-md-offset-5">
      <button class="btn btn-lg btn-block btn-primary pages-save" v-on:click="savePages()">Save</button>
    </div>
  </div>
</template>

<script>
import PageList from './PageList.vue'
  import auth from '../auth'

  export default {
    name: 'main',
    data () {
      return {
        all_pages: [],
        env1_pages: [],
        env2_pages: [],
        env3_pages: [],
        empty_pages: []
      }
    },
    components: {
      PageList
    },
    methods: {
      fetchPages() {
        this.$http.get('http://localhost:8080/api/pages').then(response => {
          this.all_pages = response.body.pages
          this.env1_pages = this.filterBy(response.body.pages, "Env1")
          this.env2_pages = this.filterBy(response.body.pages, "Env2")
          this.env3_pages = this.filterBy(response.body.pages, "Env3")
        }, response => {
          if (response.status == 401) {
            this.$notify({
              type: 'error',
              title: 'Authentication Failure',
              text: 'Failed to current page states'
            });
          } else {
            this.$notify({
              type: 'error',
              title: 'Server Error',
              text: 'Failed to current page states'
            });
          }
        })
      },
      savePages() {
        // TODO: instead of showing notifications separately for each ajax call, we should probably
        // TODO: use Promise.all to wait for all ajax calls and then generate a single notification
        // TODO: with a summary
        for (var i = 0; i < this.all_pages.length; i++) {
          this.savePage(this.all_pages[i])
        }
      },
      savePage(page) {
        var uri = 'http://localhost:8080/api/pages/' + page.name

        var successHandler = (response) => {
          if (response.body.updated === 'true') {
            this.$notify({
              type: 'success',
              title: 'Update successful',
              duration: 5000,
              text: 'Page ' + page.name + ' ' + (page.active ? 'activated' : 'deactivated')
            });
          }
        }

        var errorHandler = (response) => {
          var errorMessage = 'Failed to update page ' + page.name
          var errorTitle

          if (response.status === 401) {
            errorTitle = 'Authentication Failure'
          } else {
            errorTitle = 'Server Error'
          }

          this.$notify({
            type: 'error',
            title: errorTitle,
            duration: 5000,
            text: errorMessage
          });
        }

        if (page.active == true) {
          this.$http.put(uri).then(successHandler, errorHandler)
        } else {
          this.$http.delete(uri).then(successHandler, errorHandler)
        }
      },
      filterBy(list, value){
        value = value.charAt(0).toUpperCase() + value.slice(1);
        return list.filter(function(page){
          return page.name.startsWith(value)
        });
      }
    },
    created(){
      this.fetchPages()
    }
  }
</script>

<style>
.pages-save {
  margin-bottom: 50px;
}
</style>
