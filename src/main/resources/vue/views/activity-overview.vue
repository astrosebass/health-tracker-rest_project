<template id="activity-overview">
  <app-layout>
    <div class="container mt-4">
      <h3>Registered Activities</h3>

      <table class="table table-striped table-bordered">
        <thead>
        <tr>
          <th>ID</th>
          <th>Description</th>
          <th>Duration (minutes)</th>
          <th>Calories</th>
          <th>Started</th>
          <th>User ID</th>
        </tr>
        </thead>

        <tbody>
        <tr v-for="activity in activities" :key="activity.id">
          <td>{{ activity.id }}</td>
          <td>{{ activity.description }}</td>
          <td>{{ activity.duration }}</td>
          <td>{{ activity.calories }}</td>
          <td>{{ activity.started }}</td>
          <td>{{ activity.userId }}</td>
        </tr>
        </tbody>
      </table>

      <p v-if="activities.length === 0">No activities found.</p>
    </div>
  </app-layout>
</template>

<script>
app.component("activity-overview", {
  template: "#activity-overview",
  data: () => ({
    activities: [],
  }),
  created() {
    this.fetchActivities();
  },
  methods: {
    fetchActivities: function () {
      axios.get("/api/activities")
          .then(res => this.activities = res.data)
          .catch(() => alert("Error while fetching activities"));
    }
  }
});
</script>