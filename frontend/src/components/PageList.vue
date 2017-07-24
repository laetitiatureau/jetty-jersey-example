<template>
  <div>
  <h3>{{ title }}</h3>
  <table class="table table-striped">
    <tbody>
      <tr v-for="page in pages">
        <td><span>{{ page.name }}</span></td>
        <td>
          <bootstrap-toggle v-model="page.active" :options="{ on: 'On', off: 'Off' }"/>
        </td>
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
    props: ['title', 'pages'],
    data () {
        return {
        }
    }
}
</script>

<style>
table > tbody > tr > td:last-child {
  width: 50px;
}

table > tbody > tr > td {
  vertical-align: text-top;
}

table > tbody > tr  {
  vertical-align: middle;
}

</style>
