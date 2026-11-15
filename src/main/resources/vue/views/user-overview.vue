<template id="user-overview">
  <app-layout>
    <div class="container mt-4">
      <h3>Registered Users</h3>

      <table class="table table-striped table-bordered">
        <thead>
        <tr>
          <th>ID</th>
          <th>Name</th>
          <th>Email</th>
        </tr>
        </thead>

        <tbody>
        <tr v-for="user in users" :key="user.id">
          <td>
            <a :href="`/users/${user.id}`">{{ user.id }}</a>
          </td>
          <td>{{ user.name }}</td>
          <td>{{ user.email }}</td>
        </tr>
        </tbody>
      </table>

      <p v-if="users.length === 0">No users found.</p>
    </div>
  </app-layout>
</template>

<script>
app.component("user-overview", {
  template: "#user-overview",
  data: () => ({
    users: [],
  }),
  created() {
    this.fetchUsers();
  },
  methods: {
    fetchUsers: function () {
      axios.get("/api/users")
          .then(res => this.users = res.data)
          .catch(() => alert("Error while fetching users"));
    }
  }
});
</script>