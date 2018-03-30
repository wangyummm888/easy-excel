<template>
  <div id="app">
    <el-container style="height: 100%; border: 1px solid #eee">



        <el-container>
          <el-header style="text-align: center; font-size: 12px" height="300px" >
            <el-form :model="queryForm" ref="queryForm" label-width="500px">
              <div class="module-container ">
                <div class="module-content">
                  <el-form :inline="true" :model="formInline" class="demo-form-inline">
                    <el-form-item label="审批人">
                      <el-input v-model="formInline.user" placeholder="审批人"></el-input>
                    </el-form-item>
                    <el-form-item label="活动区域">
                      <el-select v-model="formInline.region" placeholder="活动区域">
                        <el-option label="区域一" value="shanghai"></el-option>
                        <el-option label="区域二" value="beijing"></el-option>
                      </el-select>
                    </el-form-item>
                    <div class="btns" style="text-align: center;">
                      <el-button @click="search" type="primary">查询</el-button>
                    </div>
                  </el-form>
                </div>
              </div>
            </el-form>
          </el-header>

          <div class="lists-cantainer">
            <div class="btns">
              <el-button
                type="blue"
                @click="goEditPage"
                :disabled="selectedRow.length < 1"
                v-permission:0203010002="vamLoginUserAuthorityCodes"
              >更正车架号</el-button>
              <el-button
                type="blue"
                @click="goEditPage"
                :disabled="selectedRow.length < 1"
                v-permission:0203010002="vamLoginUserAuthorityCodes"
              >申请退保</el-button>
              <el-button
                type="blue"
                @click="goEditPage"
                :disabled="selectedRow.length < 1"
                v-permission:0203010002="vamLoginUserAuthorityCodes"
              >申请批改</el-button>
              <el-button
                type="blue"
                @click="check"
                :disabled="!queryResult && !queryResult.length"
                v-permission:0203010002="vamLoginUserAuthorityCodes"
              >重新校验</el-button>
            </div>
            <el-table
              ref="multipleTable"
              v-loading.body="tableLoading"
              border
              :data="queryResult"
              tooltip-effect="dark"
              class="lists-table"
              style="width: 100%"
              @selection-change="handleSelectionChange">
              <el-table-column
                align="center"
                type="selection"
                width="40"
              />
              <el-table-column
                align="center"
                label="保单号"
                min-width="200">
                <template slot-scope="scope">
                  <a href="javascript:;" @click="openPage(scope.row.id)" v-text="scope.row.policyNo" />
                </template>
              </el-table-column>
              <el-table-column
                align="center"
                prop="companyName"
                label="保险公司"
                min-width="120" />
              <el-table-column
                align="center"
                prop="insuranceTypeStr"
                label="保险类型"
                width="100" />
              <el-table-column
                align="center"
                prop="frameNo"
                label="车架号"
                width="160" />
              <el-table-column
                align="center"
                prop="beginTime"
                label="起保日期"
                min-width="100" />
              <el-table-column
                align="center"
                prop="endTime"
                label="止保日期"
                min-width="100" />
              <el-table-column
                align="center"
                prop="policyStatusStr"
                label="状态"
                width="100" />
            </el-table>


          <el-main>
            <el-table :data="tableData" align="center">
              <el-table-column prop="date" label="日期" width="140">
              </el-table-column>
              <el-table-column prop="name" label="姓名" width="120">
              </el-table-column>
              <el-table-column prop="address" label="地址">
              </el-table-column>
            </el-table>
          </el-main>
        </el-container>
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
    },
    data() {
      return {
        formInline: {
          user: '',
          region: ''
        }
      }
    },
    methods: {
      onSubmit() {
        console.log('submit!');
      }
    }


  };

</script>

<style>
  body {
    font-family: Helvetica, sans-serif;
  }
</style>
