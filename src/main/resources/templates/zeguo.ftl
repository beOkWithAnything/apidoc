<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>${projectName}</title>
    <!-- 引入样式 -->
    <link rel="stylesheet" href="https://unpkg.com/element-ui/lib/theme-chalk/index.css"/>
    <link rel="stylesheet" href="../static/yydoc.css"/>
</head>
<body>
    <div id="yy-box">
        <el-container>
            <el-aside>
                <div class="title">
                    <span>YY - Doc</span>
                </div>
                <el-menu
                        default-active="1"
                        background-color="#ffffff"
                        active-text-color="#f47983"
                        class="el-menu-vertical-demo"
                        @open="handleOpen"
                        @close="handleClose">
                <#list controllerList as controller>
                    <el-submenu index="${controller.clazz}">
                        <template slot="title">
                            <span>${controller.name}</span>
                        </template>
                        <#list controller.methodInfoList as methodInfo>
                            <el-menu-item index="${methodInfo.uri}" @click="getData('/api/${controller.clazz}/${methodInfo.methodName}')">${methodInfo.name}</el-menu-item>
                        </#list>
                    </el-submenu>
                </#list>
                </el-menu>
            </el-aside>
            <el-container>
                <el-header>
                    <code>${projectName}</code>
                </el-header>
                <el-main>
                    <template v-if="response === ''">
                        <div class="main">
                            <b class="hello">Hello YY - Doc</b>
                            <b class="hello">${url}</b>
                        </div>
                    </template>
                    <template v-else>
                        <div class="main">
                            <h2>{{ response.name }}</h2>
                            <el-divider></el-divider>

                            <h3>请求方式</h3>
                            <pre><code>{{ response.method }}</code></pre>

                            <h3>请求URI</h3>
                            <pre><code id="text_1" @click="copy">{{ response.uri }}</code></pre>
                            <input id="input_1" type="text"/>

                            <h3>请求示例</h3>
                            <pre><code>{<span class="json" v-for="item in response.jsonPara">{{ item }}</span>}</code></pre>

                            <h3>请求参数</h3>
                            <div class="table-box">
                                <template>
                                    <el-table
                                            :data="table"
                                            stripe
                                            style="width: 100%"
                                    >
                                        <el-table-column
                                                prop="name"
                                                label="参数名"
                                        ></el-table-column>
                                        <el-table-column
                                                prop="need"
                                                label="是否必须"
                                        ></el-table-column>
                                        <el-table-column
                                                prop="clazz"
                                                label="数据类型"
                                        ></el-table-column>
                                        <el-table-column
                                                prop="description"
                                                label="说明"
                                        ></el-table-column>
                                        <el-table-column
                                                prop="demo"
                                                label="示例值"
                                        ></el-table-column>
                                    </el-table>
                                </template>
                            </div>


                        </div>
                    </template>
                </el-main>
            </el-container>
        </el-container>
    </div>

    <!-- import Vue before Element -->
    <script src="https://unpkg.com/vue/dist/vue.js"></script>
    <!-- 引入组件库 -->
    <script src="https://unpkg.com/element-ui/lib/index.js"></script>
    <script src="https://unpkg.com/axios/dist/axios.min.js"></script>
    <script>
        new Vue({
            el: '#yy-box',
            data: {
                response : '',
                table: []
            },
            methods: {
                handleOpen: function(key, keyPath) {
                    console.log(key, keyPath);
                },
                handleClose: function(key, keyPath) {
                    console.log(key, keyPath);
                },
                copy: function() {
                    var text_1 = document.getElementById('text_1').innerText;
                    var input_1 = document.getElementById('input_1');
                    input_1.value = text_1;
                    console.log(input_1.value);
                    input_1.select();
                    document.execCommand("copy");
                    this.$message('复制成功')
                },
                getData: function(url) {
                    var that = this;
                    console.log(url);
                    axios.get(url)
                        .then(function (res) {
                            console.log(res.data);
                            that.response = res.data;
                            that.table = res.data.parameterInfoList;
                        })
                }
            }
        })
    </script>
</body>
</html>