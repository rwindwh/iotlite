<template>
  <div>
    <b-form @submit="onSubmit" >
      <b-form-group
        
        label="Name:"
        description="gateway name"
      >
        <b-form-input
          v-model="form.name"
          type="text"
          required
          placeholder="Enter gateway Name"
        ></b-form-input>
      </b-form-group>
      <b-form-group
        
        label="Desc:"
        description="gateway desc"
      >
        <b-form-input
          v-model="form.description"
          type="text"
          required
          placeholder="enter gateway description"
        ></b-form-input>
      </b-form-group>

      <b-form-group
        
        label="Plugin:"
        description="gateway adapter"
      >
        <b-form-file v-model="file" ref="file-input" class="mb-2"></b-form-file>
        <b-form-datalist id="type-list" :options="gatewayType"></b-form-datalist>
      </b-form-group>
       <b-button type="submit" variant="primary">Submit</b-button>
    </b-form>
  </div>
</template>

<script>
export default {
  name:"New",
  data(){
    return {
      gatewayType:[
          "Self Host","Amazon","Ali","Onenet","Ctwing"
      ],
      form:{

      }
    }
  },
  methods:{
    onSubmit(){

    }
  }
}
</script>

<style scoped>

</style>