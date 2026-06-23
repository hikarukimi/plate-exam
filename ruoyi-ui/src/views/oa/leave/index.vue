<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="申请人" prop="userName">
        <el-input v-model="queryParams.userName" placeholder="请输入申请人" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="请假类型" prop="leaveType">
        <el-select v-model="queryParams.leaveType" placeholder="请假类型" clearable>
          <el-option v-for="dict in dict.type.oa_leave_type" :key="dict.value" :label="dict.label" :value="dict.value" />
        </el-select>
      </el-form-item>
      <el-form-item label="流程状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="流程状态" clearable>
          <el-option v-for="dict in dict.type.oa_leave_status" :key="dict.value" :label="dict.label" :value="dict.value" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain icon="el-icon-plus" size="mini" @click="handleAdd" v-hasPermi="['oa:leave:add']">发起请假</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain icon="el-icon-delete" size="mini" :disabled="multiple" @click="handleDelete" v-hasPermi="['oa:leave:remove']">删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="warning" plain icon="el-icon-download" size="mini" @click="handleExport" v-hasPermi="['oa:leave:export']">导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="leaveList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="编号" align="center" prop="leaveId" width="60" />
      <el-table-column label="申请人" align="center" prop="userName" />
      <el-table-column label="请假类型" align="center" prop="leaveType">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.oa_leave_type" :value="scope.row.leaveType" />
        </template>
      </el-table-column>
      <el-table-column label="开始日期" align="center" prop="startDate" width="100">
        <template slot-scope="scope"><span>{{ parseTime(scope.row.startDate, '{y}-{m}-{d}') }}</span></template>
      </el-table-column>
      <el-table-column label="结束日期" align="center" prop="endDate" width="100">
        <template slot-scope="scope"><span>{{ parseTime(scope.row.endDate, '{y}-{m}-{d}') }}</span></template>
      </el-table-column>
      <el-table-column label="天数" align="center" prop="days" width="60" />
      <el-table-column label="事由" align="center" prop="reason" :show-overflow-tooltip="true" />
      <el-table-column label="流程状态" align="center" prop="status">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.oa_leave_status" :value="scope.row.status" />
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" width="220" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button size="mini" type="text" icon="el-icon-view" @click="handleView(scope.row)">详情</el-button>
          <el-button v-if="scope.row.status === 'pending'" size="mini" type="text" icon="el-icon-edit" @click="handleUpdate(scope.row)" v-hasPermi="['oa:leave:edit']">修改</el-button>
          <el-button v-if="scope.row.status === 'pending'" size="mini" type="text" icon="el-icon-s-check" @click="handleApprove(scope.row)" v-hasPermi="['oa:leave:approve']">审批</el-button>
          <el-button v-if="scope.row.status === 'approved'" size="mini" type="text" icon="el-icon-folder-checked" @click="handleArchive(scope.row)" v-hasPermi="['oa:leave:archive']">归档</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total>0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize" @pagination="getList" />

    <!-- 发起/修改请假对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="600px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="90px">
        <el-form-item label="请假类型" prop="leaveType">
          <el-select v-model="form.leaveType" placeholder="请选择请假类型" style="width:100%">
            <el-option v-for="dict in dict.type.oa_leave_type" :key="dict.value" :label="dict.label" :value="dict.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="开始日期" prop="startDate">
          <el-date-picker v-model="form.startDate" type="date" value-format="yyyy-MM-dd" placeholder="选择开始日期" style="width:100%" />
        </el-form-item>
        <el-form-item label="结束日期" prop="endDate">
          <el-date-picker v-model="form.endDate" type="date" value-format="yyyy-MM-dd" placeholder="选择结束日期" style="width:100%" />
        </el-form-item>
        <el-form-item label="请假天数" prop="days">
          <el-input-number v-model="form.days" :min="0.5" :step="0.5" controls-position="right" style="width:100%" />
        </el-form-item>
        <el-form-item label="请假事由" prop="reason">
          <el-input v-model="form.reason" type="textarea" placeholder="请输入请假事由" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>

    <!-- 审批对话框 -->
    <el-dialog title="请假审批" :visible.sync="approveOpen" width="500px" append-to-body>
      <el-form ref="approveForm" :model="approveForm" label-width="90px">
        <el-form-item label="审批意见">
          <el-input v-model="approveForm.approveRemark" type="textarea" placeholder="请输入审批意见" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="success" @click="submitApprove(true)">通 过</el-button>
        <el-button type="danger" @click="submitApprove(false)">驳 回</el-button>
        <el-button @click="approveOpen = false">取 消</el-button>
      </div>
    </el-dialog>

    <!-- 详情对话框 -->
    <el-dialog title="请假详情" :visible.sync="viewOpen" width="600px" append-to-body>
      <el-steps :active="flowActive" :align-center="true" finish-status="success" :status="flowStatus" style="margin-bottom:20px">
        <el-step title="提交申请" :description="parseTime(form.createTime, '{y}-{m}-{d}')" />
        <el-step :title="form.status === 'rejected' ? '已驳回' : '部门审批'" :description="parseTime(form.approveTime, '{y}-{m}-{d}')" />
        <el-step title="管理员归档" :description="parseTime(form.archiveTime, '{y}-{m}-{d}')" />
      </el-steps>
      <el-descriptions :column="2" border>
        <el-descriptions-item label="申请人">{{ form.userName }}</el-descriptions-item>
        <el-descriptions-item label="流程状态"><dict-tag :options="dict.type.oa_leave_status" :value="form.status" /></el-descriptions-item>
        <el-descriptions-item label="请假类型"><dict-tag :options="dict.type.oa_leave_type" :value="form.leaveType" /></el-descriptions-item>
        <el-descriptions-item label="天数">{{ form.days }}</el-descriptions-item>
        <el-descriptions-item label="开始日期">{{ parseTime(form.startDate, '{y}-{m}-{d}') }}</el-descriptions-item>
        <el-descriptions-item label="结束日期">{{ parseTime(form.endDate, '{y}-{m}-{d}') }}</el-descriptions-item>
        <el-descriptions-item label="事由" :span="2">{{ form.reason }}</el-descriptions-item>
        <el-descriptions-item label="审批意见" :span="2">{{ form.approveRemark }}</el-descriptions-item>
        <el-descriptions-item label="审批时间">{{ parseTime(form.approveTime) }}</el-descriptions-item>
        <el-descriptions-item label="归档时间">{{ parseTime(form.archiveTime) }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script>
import { listLeave, getLeave, addLeave, updateLeave, approveLeave, archiveLeave, delLeave } from "@/api/oa/leave"

export default {
  name: "Leave",
  dicts: ['oa_leave_type', 'oa_leave_status'],
  data() {
    return {
      loading: true,
      ids: [],
      multiple: true,
      showSearch: true,
      total: 0,
      leaveList: [],
      title: "",
      open: false,
      approveOpen: false,
      viewOpen: false,
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        userName: undefined,
        leaveType: undefined,
        status: undefined
      },
      form: {},
      approveForm: {},
      rules: {
        leaveType: [{ required: true, message: "请假类型不能为空", trigger: "change" }],
        startDate: [{ required: true, message: "开始日期不能为空", trigger: "blur" }],
        endDate: [{ required: true, message: "结束日期不能为空", trigger: "blur" }],
        days: [{ required: true, message: "请假天数不能为空", trigger: "blur" }]
      }
    }
  },
  created() {
    this.getList()
  },
  computed: {
    // 工作流进度：提交=1 审批通过=2 归档=3；驳回时停在审批步并标红
    flowActive() {
      const map = { pending: 1, approved: 2, rejected: 1, archived: 3 }
      return map[this.form.status] || 0
    },
    flowStatus() {
      return this.form.status === 'rejected' ? 'error' : 'process'
    }
  },
  methods: {
    getList() {
      this.loading = true
      listLeave(this.queryParams).then(response => {
        this.leaveList = response.rows
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
        leaveId: undefined,
        leaveType: undefined,
        startDate: undefined,
        endDate: undefined,
        days: undefined,
        reason: undefined
      }
      this.resetForm("form")
    },
    handleQuery() {
      this.queryParams.pageNum = 1
      this.getList()
    },
    resetQuery() {
      this.resetForm("queryForm")
      this.handleQuery()
    },
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.leaveId)
      this.multiple = !selection.length
    },
    handleAdd() {
      this.reset()
      this.open = true
      this.title = "发起请假申请"
    },
    handleUpdate(row) {
      this.reset()
      getLeave(row.leaveId).then(response => {
        this.form = response.data
        this.open = true
        this.title = "修改请假申请"
      })
    },
    handleView(row) {
      getLeave(row.leaveId).then(response => {
        this.form = response.data
        this.viewOpen = true
      })
    },
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (!valid) return
        if (this.form.leaveId != undefined) {
          updateLeave(this.form).then(() => {
            this.$modal.msgSuccess("修改成功")
            this.open = false
            this.getList()
          })
        } else {
          addLeave(this.form).then(() => {
            this.$modal.msgSuccess("提交成功")
            this.open = false
            this.getList()
          })
        }
      })
    },
    handleApprove(row) {
      this.approveForm = { leaveId: row.leaveId, approveRemark: undefined }
      this.approveOpen = true
    },
    submitApprove(pass) {
      approveLeave(pass, this.approveForm).then(() => {
        this.$modal.msgSuccess(pass ? "已通过" : "已驳回")
        this.approveOpen = false
        this.getList()
      })
    },
    handleArchive(row) {
      this.$modal.confirm('确认归档该请假申请？归档后将自动登记对应日期的考勤记录。').then(() => {
        return archiveLeave(row.leaveId)
      }).then(() => {
        this.$modal.msgSuccess("归档成功")
        this.getList()
      }).catch(() => {})
    },
    handleDelete(row) {
      const leaveIds = row.leaveId || this.ids
      this.$modal.confirm('是否确认删除请假申请编号为"' + leaveIds + '"的数据项？').then(() => {
        return delLeave(leaveIds)
      }).then(() => {
        this.getList()
        this.$modal.msgSuccess("删除成功")
      }).catch(() => {})
    },
    handleExport() {
      this.download('oa/leave/export', { ...this.queryParams }, `leave_${new Date().getTime()}.xlsx`)
    }
  }
}
</script>
