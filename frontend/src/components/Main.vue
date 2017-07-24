<template>
  <div class="row">
      <h2 class="text-center">Maintenance Pages</h2>
      <div class="col-xs-6 col-xs-offset-3">
          <PageList filter="Env1"/>
      </div>
      <div class="col-xs-6 col-xs-offset-3">
          <PageList filter="Env2"/>
      </div>
      <div class="col-xs-6 col-xs-offset-3">
          <PageList filter="Env3"/>
      </div>
      <div class="col-xs-2 col-xs-offset-5">
      <button class="btn btn-block btn-primary">Save</button>
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
</style>
