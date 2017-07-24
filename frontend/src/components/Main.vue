<template>
  <div class="row">
      <h2 class="text-center">Maintenance Pages</h2>
      <div class="col-xs-12 col-sm-8 col-md-6 col-sm-offset-2 col-md-offset-3">
          <PageList filter="Env1"/>
      </div>
      <div class="col-xs-12 col-sm-8 col-md-6 col-sm-offset-2 col-md-offset-3">
          <PageList filter="Env2"/>
      </div>
      <div class="col-xs-12 col-sm-8 col-md-6 col-sm-offset-2 col-md-offset-3">
          <PageList filter="Env3"/>
      </div>
      <div class="col-xs-12 col-sm-4 col-md-2 col-sm-offset-4 col-md-offset-5">
      <button class="btn btn-lg btn-block btn-primary pages-save">Save</button>
    </div>
  </div>
</template>

<script>
import PageList from './PageList.vue'

export default {
  name: 'main',
  data () {
    return {
    }
  },
  components: {
      PageList
  }
}
</script>

<style>
.pages-save {
  margin-bottom: 50px;
}
</style>
