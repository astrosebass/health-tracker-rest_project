<template id="user-activity-overview">
  <app-layout>
    <div class="container mt-4">
      <h3>Activities list</h3>

      <table class="table table-striped table-bordered">
        <thead>
        <tr>
          <th>ID</th>
          <th>Description</th>
          <th>Duration (minutes)</th>
        </tr>
        </thead>

        <tbody>
        <tr v-for="activity in activities" :key="activity.id">
          <td>{{ activity.id }}</td>
          <td>{{ activity.description }}</td>
          <td>{{ activity.duration }}</td>
        </tr>
        </tbody>
      </table>

      <p v-if="activities.length === 0">
        No activities found for this user.
      </p>
    </div>
  </app-layout>
</template>

<script>
app.component("user-activity-overview", {
  template: "#user-activity-overview",
  data: () => ({
    activities: [],
  }),
  created() {
    const userId = this.$javalin.pathParams["user-id"];
    axios.get(`/api/users/${userId}/activities`)
        .then(res => this.activities = res.data)
        .catch(() => alert("Error while fetching activities"));
  }
});
</script>