<template>
  <div id="app">
    <el-container style="height: 100%; border: 1px solid #eee">
      <el-aside width="200px"  style="background-color: #545c64">
        <el-menu :default-openeds="[]"
                 background-color="#545c64"
                 text-color="#fff"
                 active-text-color="#ffd04b">
          <el-submenu index="1">
            <template slot="title">导出功能</template>
              <el-menu-item index="1-1">学生列表</el-menu-item>
          </el-submenu>

          <el-submenu index="2">
            <template slot="title">导入功能</template>
            <el-menu-item index="2-1">学生列表</el-menu-item>
          </el-submenu>
        </el-menu>
      </el-aside>

      <el-container>
        <el-header style="text-align: right; font-size: 12px">
          <el-dropdown>
            <i class="el-icon-setting" style="margin-right: 15px"></i>
            <el-dropdown-menu slot="dropdown">
              <el-dropdown-item>查看</el-dropdown-item>
              <el-dropdown-item>新增</el-dropdown-item>
              <el-dropdown-item>删除</el-dropdown-item>
            </el-dropdown-menu>
          </el-dropdown>
          <span>王小虎</span>
        </el-header>





        <el-main>
          <el-table :data="tableData">
            <el-table-column prop="date" label="日期" width="140">
            </el-table-column>
            <el-table-column prop="name" label="姓名" width="120">
            </el-table-column>
            <el-table-column prop="address" label="地址">
            </el-table-column>
          </el-table>
        </el-main>
      </el-container>




      <el-form :model="queryForm" ref="queryForm" label-width="100px">
        <div class="module-container">
          <div class="module-content">
            <el-row>


             <!-- <el-col :span="6">
                <el-form-item label="快递单号">
                  <el-input v-model="queryForm.expressNo" placeholder="请输入快递单号" style="width: 100%" />
                </el-form-item>
              </el-col>
              <el-col :span="6">
                <el-form-item label="状态">
                  <el-select clearable v-model="queryForm.invoiceStatus" placeholder="请选择">
                    <el-option
                      v-for="item in status"
                      :key="item.value"
                      :label="item.label"
                      :value="item.value" />
                  </el-select>
                </el-form-item>
              </el-col>-->
            </el-row>
            <el-row style="text-align: center;">
              <el-button @click="getList" type="primary">查询</el-button>
            </el-row>
          </div>
        </div>
      </el-form>
    </el-container>




  </div>
</template>


<style>
  .el-header {
    background-color: #B3C0D1;
    color: #333;
    line-height: 60px;
  }

  .el-aside {
    color: #333;
  }
</style>


<script>
  export default {
    data() {
      const item = {
        date: '2016-05-02',
        name: '王小虎',
        address: '上海市普陀区金沙江路 1518 弄'
      };
      return {
        tableData: Array(10).fill(item)
      }
    }
  };

</script>





<style>
  body {
    font-family: Helvetica, sans-serif;
  }
</style>
