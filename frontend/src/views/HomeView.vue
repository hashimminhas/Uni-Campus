<template>
  <div>

    <!-- ───────────── 1. CREATE STUDENT ───────────── -->
    <section style="margin-bottom: 32px;">
      <h2>1. Create Student</h2>
      <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 8px; max-width: 500px;">
        <input v-model="create.firstName"    placeholder="First Name"      style="padding: 6px;" />
        <input v-model="create.lastName"     placeholder="Last Name"       style="padding: 6px;" />
        <input v-model="create.email"        placeholder="Email"           style="padding: 6px;" />
        <input v-model="create.phoneNumber"  placeholder="Phone (optional)" style="padding: 6px;" />
        <input v-model="create.program"      placeholder="Program"         style="padding: 6px;" />
        <input v-model="create.enrollmentYear" placeholder="Enrollment Year (e.g. 2024)" type="number" style="padding: 6px;" />
      </div>
      <button @click="createStudent" style="margin-top: 10px; padding: 8px 20px;">Create</button>
      <pre v-if="create.response" :style="responseStyle(create.isError)">{{ create.response }}</pre>
    </section>

    <hr />

    <!-- ───────────── 2. FIND STUDENT ───────────── -->
    <section style="margin-bottom: 32px;">
      <h2>2. Find Student by ID</h2>
      <input v-model="find.studentId" placeholder="Student UUID" style="padding: 6px; width: 340px;" />
      <button @click="findStudent" style="margin-left: 8px; padding: 8px 20px;">Find</button>
      <pre v-if="find.response" :style="responseStyle(find.isError)">{{ find.response }}</pre>
    </section>

    <hr />

    <!-- ───────────── 3. UPDATE STUDENT ───────────── -->
    <section style="margin-bottom: 32px;">
      <h2>3. Update Student Profile</h2>
      <input v-model="update.studentId" placeholder="Student UUID" style="padding: 6px; width: 340px; display: block; margin-bottom: 8px;" />
      <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 8px; max-width: 500px;">
        <input v-model="update.firstName"     placeholder="First Name (optional)"    style="padding: 6px;" />
        <input v-model="update.lastName"      placeholder="Last Name (optional)"     style="padding: 6px;" />
        <input v-model="update.email"         placeholder="Email (optional)"         style="padding: 6px;" />
        <input v-model="update.phoneNumber"   placeholder="Phone (optional)"         style="padding: 6px;" />
        <input v-model="update.program"       placeholder="Program (optional)"       style="padding: 6px;" />
        <input v-model="update.enrollmentYear" placeholder="Enrollment Year (optional)" type="number" style="padding: 6px;" />
      </div>
      <button @click="updateStudent" style="margin-top: 10px; padding: 8px 20px;">Update</button>
      <pre v-if="update.response" :style="responseStyle(update.isError)">{{ update.response }}</pre>
    </section>

    <hr />

    <!-- ───────────── 4. UPDATE STATUS ───────────── -->
    <section style="margin-bottom: 32px;">
      <h2>4. Update Academic Status</h2>
      <input v-model="status.studentId" placeholder="Student UUID" style="padding: 6px; width: 340px;" />
      <select v-model="status.academicStatus" style="margin-left: 8px; padding: 6px;">
        <option value="ACTIVE">ACTIVE</option>
        <option value="SUSPENDED">SUSPENDED</option>
        <option value="GRADUATED">GRADUATED</option>
        <option value="ON_LEAVE">ON_LEAVE</option>
      </select>
      <button @click="updateStatus" style="margin-left: 8px; padding: 8px 20px;">Update Status</button>
      <pre v-if="status.response" :style="responseStyle(status.isError)">{{ status.response }}</pre>
    </section>

    <hr />

    <!-- ───────────── 5. VALIDATE STUDENT ───────────── -->
    <section style="margin-bottom: 32px;">
      <h2>5. Validate Student</h2>
      <input v-model="validate.studentId" placeholder="Student UUID" style="padding: 6px; width: 340px;" />
      <button @click="validateStudent" style="margin-left: 8px; padding: 8px 20px;">Validate</button>
      <pre v-if="validate.response" :style="validateStyle">{{ validate.response }}</pre>
    </section>

    <hr />

    <!-- ───────────── 6. DELETE STUDENT ───────────── -->
    <section style="margin-bottom: 32px;">
      <h2>6. Delete Student</h2>
      <input v-model="del.studentId" placeholder="Student UUID" style="padding: 6px; width: 340px;" />
      <button @click="deleteStudent" style="margin-left: 8px; padding: 8px 20px; background: #e74c3c; color: white; border: none; cursor: pointer;">Delete</button>
      <pre v-if="del.response" :style="responseStyle(del.isError)">{{ del.response }}</pre>
    </section>

  </div>
</template>

<script>
const BASE = 'http://localhost:8081'

export default {
  name: 'HomeView',

  data() {
    return {
      create: {
        firstName: '', lastName: '', email: '', phoneNumber: '',
        program: '', enrollmentYear: '',
        response: null, isError: false
      },
      find: {
        studentId: '', response: null, isError: false
      },
      update: {
        studentId: '', firstName: '', lastName: '', email: '',
        phoneNumber: '', program: '', enrollmentYear: '',
        response: null, isError: false
      },
      status: {
        studentId: '', academicStatus: 'ACTIVE', response: null, isError: false
      },
      validate: {
        studentId: '', response: null, isValid: null
      },
      del: {
        studentId: '', response: null, isError: false
      }
    }
  },

  computed: {
    validateStyle() {
      if (this.validate.isValid === true)  return 'background:#d4edda; color:#155724; padding:10px; border-radius:4px; white-space:pre-wrap;'
      if (this.validate.isValid === false) return 'background:#f8d7da; color:#721c24; padding:10px; border-radius:4px; white-space:pre-wrap;'
      return 'background:#f8f9fa; padding:10px; border-radius:4px; white-space:pre-wrap;'
    }
  },

  methods: {
    responseStyle(isError) {
      const base = 'padding:10px; border-radius:4px; white-space:pre-wrap; margin-top:8px;'
      return isError
        ? base + 'background:#f8d7da; color:#721c24;'
        : base + 'background:#d4edda; color:#155724;'
    },

    async createStudent() {
      this.create.response = null
      const body = {
        firstName: this.create.firstName,
        lastName:  this.create.lastName,
        email:     this.create.email,
        program:   this.create.program,
        enrollmentYear: this.create.enrollmentYear ? Number(this.create.enrollmentYear) : undefined
      }
      if (this.create.phoneNumber) body.phoneNumber = this.create.phoneNumber

      try {
        const res = await fetch(`${BASE}/students`, {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify(body)
        })
        const data = await res.json()
        this.create.isError = !res.ok
        this.create.response = JSON.stringify(data, null, 2)
      } catch (e) {
        this.create.isError = true
        this.create.response = 'Error: ' + e.message
      }
    },

    async findStudent() {
      this.find.response = null
      try {
        const res = await fetch(`${BASE}/students/${this.find.studentId}`)
        const data = await res.json()
        this.find.isError = !res.ok
        this.find.response = JSON.stringify(data, null, 2)
      } catch (e) {
        this.find.isError = true
        this.find.response = 'Error: ' + e.message
      }
    },

    async updateStudent() {
      this.update.response = null
      const body = {}
      if (this.update.firstName)     body.firstName     = this.update.firstName
      if (this.update.lastName)      body.lastName      = this.update.lastName
      if (this.update.email)         body.email         = this.update.email
      if (this.update.phoneNumber)   body.phoneNumber   = this.update.phoneNumber
      if (this.update.program)       body.program       = this.update.program
      if (this.update.enrollmentYear) body.enrollmentYear = Number(this.update.enrollmentYear)

      try {
        const res = await fetch(`${BASE}/students/${this.update.studentId}`, {
          method: 'PUT',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify(body)
        })
        const data = await res.json()
        this.update.isError = !res.ok
        this.update.response = JSON.stringify(data, null, 2)
      } catch (e) {
        this.update.isError = true
        this.update.response = 'Error: ' + e.message
      }
    },

    async updateStatus() {
      this.status.response = null
      try {
        const res = await fetch(`${BASE}/students/${this.status.studentId}/status`, {
          method: 'PATCH',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify({ academicStatus: this.status.academicStatus })
        })
        const data = await res.json()
        this.status.isError = !res.ok
        this.status.response = JSON.stringify(data, null, 2)
      } catch (e) {
        this.status.isError = true
        this.status.response = 'Error: ' + e.message
      }
    },

    async validateStudent() {
      this.validate.response = null
      this.validate.isValid = null
      try {
        const res = await fetch(`${BASE}/students/validate/${this.validate.studentId}`)
        const data = await res.json()
        if (!res.ok) {
          this.validate.isValid = false
          this.validate.response = JSON.stringify(data, null, 2)
        } else {
          this.validate.isValid = data.valid
          this.validate.response = JSON.stringify(data, null, 2)
        }
      } catch (e) {
        this.validate.isValid = false
        this.validate.response = 'Error: ' + e.message
      }
    },

    async deleteStudent() {
      this.del.response = null
      try {
        const res = await fetch(`${BASE}/students/${this.del.studentId}`, {
          method: 'DELETE'
        })
        if (res.status === 204) {
          this.del.isError = false
          this.del.response = 'Deleted successfully.'
        } else {
          const data = await res.json()
          this.del.isError = true
          this.del.response = JSON.stringify(data, null, 2)
        }
      } catch (e) {
        this.del.isError = true
        this.del.response = 'Error: ' + e.message
      }
    }
  }
}
</script>
