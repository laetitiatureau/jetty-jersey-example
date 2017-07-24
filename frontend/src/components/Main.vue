<template>
  <div class="row">
      <h2>Maintenance Pages</h2>
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
