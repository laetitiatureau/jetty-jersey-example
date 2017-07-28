<template>
  <div class="row">
      <Alert v-if="alert" v-bind:message="alert"/>
      <h2 class="text-center">Maintenance Pages</h2>
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
import Alert from './Alert.vue'
import PageList from './PageList.vue'
import auth from '../auth'

export default {
  name: 'main',
  data () {
    return {
      alert: '',
      all_pages: [],
      env1_pages: [],
      env2_pages: [],
      env3_pages: []
    }
  },
  components: {
      PageList, Alert
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
              auth.logout()
              this.$router.push('/login')
            }
        })
    },
    savePages() {
        var res
        for (var i = 0; i < this.all_pages.length; i++) {
            var success = this.savePage(this.all_pages[i])
            if (success) {
                this.alert = 'error'
                return
            }
        }

        this.alert = "Your changes were saved."
        this.$router.push('/')
    },
    savePage(page) {
        if (page.active == true) {
          this.$http.put('http://localhost:8080/api/pages/' + page.name).then(response => {
            return true;
          }, response => {
            this.alert = 'error'
            if (response.status === 401) {
              auth.logout()
              this.$router.push('/login')
            }
          })
        } else {
          this.$http.delete('http://localhost:8080/api/pages/' + page.name).then(response => {
            return true;
          }, response => {
            this.alert = 'error'
            if (response.status === 401) {
              auth.logout()
              this.$router.push('/login')
            }
          })
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
