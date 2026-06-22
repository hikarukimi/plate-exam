<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="员工" prop="userName">
        <el-input v-model="queryParams.userName" placeholder="请输入员工姓名" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="考勤状态" prop="attStatus">
        <el-select v-model="queryParams.attStatus" placeholder="考勤状态" clearable>
          <el-option v-for="dict in dict.type.oa_att_status" :key="dict.value" :label="dict.label" :value="dict.value" />
        </el-select>
      </el-form-item>
      <el-form-item label="考勤日期">
        <el-date-picker v-model="dateRange" type="daterange" value-format="yyyy-MM-dd" range-separator="-" start-placeholder="开始日期" end-placeholder="结束日期" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain icon="el-icon-plus" size="mini" @click="handleAdd" v-hasPermi="['oa:attendance:add']">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="success" plain icon="el-icon-edit" size="mini" :disabled="single" @click="handleUpdate" v-hasPermi="['oa:attendance:edit']">修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain icon="el-icon-delete" size="mini" :disabled="multiple" @click="handleDelete" v-hasPermi="['oa:attendance:remove']">删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="warning" plain icon="el-icon-download" size="mini" @click="handleExport" v-hasPermi="['oa:attendance:export']">导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="attendanceList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="编号" align="center" prop="attId" width="60" />
      <el-table-column label="员工" align="center" prop="userName" />
      <el-table-column label="考勤日期" align="center" prop="attDate" width="120">
        <template slot-scope="scope"><span>{{ parseTime(scope.row.attDate, '{y}-{m}-{d}') }}</span></template>
      </el-table-column>
      <el-table-column label="考勤状态" align="center" prop="attStatus">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.oa_att_status" :value="scope.row.attStatus" />
        </template>
      </el-table-column>
      <el-table-column label="来源" align="center" prop="source">
        <template slot-scope="scope">
          <el-tag :type="scope.row.source === '1' ? 'info' : ''">{{ scope.row.source === '1' ? '请假联动' : '手动' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="备注" align="center" prop="remark" :show-overflow-tooltip="true" />
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button size="mini" type="text" icon="el-icon-edit" @click="handleUpdate(scope.row)" v-hasPermi="['oa:attendance:edit']">修改</el-button>
          <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDelete(scope.row)" v-hasPermi="['oa:attendance:remove']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total>0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize" @pagination="getList" />

    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="90px">
        <el-form-item label="员工" prop="userId">
          <el-select v-model="form.userId" placeholder="请选择员工" filterable style="width:100%" @change="handleUserChange">
            <el-option v-for="u in userOptions" :key="u.userId" :label="u.nickName" :value="u.userId" />
          </el-select>
        </el-form-item>
        <el-form-item label="考勤日期" prop="attDate">
          <el-date-picker v-model="form.attDate" type="date" value-format="yyyy-MM-dd" placeholder="选择考勤日期" style="width:100%" />
        </el-form-item>
        <el-form-item label="考勤状态" prop="attStatus">
          <el-select v-model="form.attStatus" placeholder="请选择考勤状态" style="width:100%">
            <el-option v-for="dict in dict.type.oa_att_status" :key="dict.value" :label="dict.label" :value="dict.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="form.remark" type="textarea" placeholder="请输入备注" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listAttendance, getAttendance, addAttendance, updateAttendance, delAttendance } from "@/api/oa/attendance"
import { listUser } from "@/api/system/user"

export default {
  name: "Attendance",
  dicts: ['oa_att_status'],
  data() {
    return {
      loading: true,
      ids: [],
      single: true,
      multiple: true,
      showSearch: true,
      total: 0,
      attendanceList: [],
      title: "",
      open: false,
      dateRange: [],
      userOptions: [],
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        userName: undefined,
        attStatus: undefined
      },
      form: {},
      rules: {
        userId: [{ required: true, message: "请选择员工", trigger: "change" }],
        attDate: [{ required: true, message: "考勤日期不能为空", trigger: "blur" }],
        attStatus: [{ required: true, message: "考勤状态不能为空", trigger: "change" }]
      }
    }
  },
  created() {
    this.getList()
    this.loadUsers()
  },
  methods: {
    loadUsers() {
      listUser({ pageNum: 1, pageSize: 1000 }).then(response => {
        this.userOptions = response.rows
      })
    },
    handleUserChange(userId) {
      const u = this.userOptions.find(i => i.userId === userId)
      if (u) {
        this.form.userName = u.nickName
        this.form.deptId = u.deptId
      }
    },
    getList() {
      this.loading = true
      const params = { ...this.queryParams }
      if (this.dateRange && this.dateRange.length === 2) {
        params.params = { beginAttDate: this.dateRange[0], endAttDate: this.dateRange[1] }
      }
      listAttendance(params).then(response => {
        this.attendanceList = response.rows
        this.total = response.total
        this.loading = false
      })
    },
    cancel() {
      this.open = false
      this.reset()
    },
    reset() {
      this.form = {
        attId: undefined,
        userId: undefined,
        userName: undefined,
        deptId: undefined,
        attDate: undefined,
        attStatus: undefined,
        remark: undefined
      }
      this.resetForm("form")
    },
    handleQuery() {
      this.queryParams.pageNum = 1
      this.getList()
    },
    resetQuery() {
      this.dateRange = []
      this.resetForm("queryForm")
      this.handleQuery()
    },
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.attId)
      this.single = selection.length !== 1
      this.multiple = !selection.length
    },
    handleAdd() {
      this.reset()
      this.open = true
      this.title = "新增考勤记录"
    },
    handleUpdate(row) {
      this.reset()
      const attId = row.attId || this.ids[0]
      getAttendance(attId).then(response => {
        this.form = response.data
        this.open = true
        this.title = "修改考勤记录"
      })
    },
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (!valid) return
        if (this.form.attId != undefined) {
          updateAttendance(this.form).then(() => {
            this.$modal.msgSuccess("修改成功")
            this.open = false
            this.getList()
          })
        } else {
          addAttendance(this.form).then(() => {
            this.$modal.msgSuccess("新增成功")
            this.open = false
            this.getList()
          })
        }
      })
    },
    handleDelete(row) {
      const attIds = row.attId || this.ids
      this.$modal.confirm('是否确认删除考勤记录编号为"' + attIds + '"的数据项？').then(() => {
        return delAttendance(attIds)
      }).then(() => {
        this.getList()
        this.$modal.msgSuccess("删除成功")
      }).catch(() => {})
    },
    handleExport() {
      this.download('oa/attendance/export', { ...this.queryParams }, `attendance_${new Date().getTime()}.xlsx`)
    }
  }
}
</script>
