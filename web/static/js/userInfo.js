 /* jshint esversion: 6 */

//import element from "element-ui"


window.onload = function () {

    // 实例化vue
    var vm = new Vue({
        el: "#all",
        data: {
            headPath: "",               //头像路径
            user: {},                   //用户信息对象
            user_other: {},             //用户信息对象副本
            infoListKeys: [],           //显示key数组
            infoListValues: [],         //显示value数组
            infoListKeys_other: [],     //缓存key数组
            infoListValues_other: [],   //缓存value数组
            isDisplay: false,           //是否显示编辑界面
            isAddAttr: false,           //是否显示添加界面
            btnAdd: "添加",             //添加按钮值
            btnEdit: "编辑",            //编辑按钮值
            activeObj: {},              //正则验证后值
            isActive: "",               //是否请求失败
            allRegexError: new Set(),   //正则验证输入不合法集和
            changeflag:true,            //编辑按钮是否可用
            addflag:true,               //添加按钮是否可用
            addattrflag:true,           //添加属性按钮是否可用
            data:{}                     //ajax返回的数据
        },
        created:function() {
            this.getAllInfo();
        },
        methods: {
            getAllInfo: function () {
                let path = this.getFileName() == "selectionUserInfo.html" ? "selectionUserInfo" : "userinfo";
                fetch(path, {
                    method: "GET",
                    headers: {
                        "Content-Type": "application/json"
                    }
                }).then(function (response) {
                    if (response.status == 200) {
                        return response.json();
                    } else {
                        alert("获取数据失败");
                    }
                }).then(function (data) {
                    //将数据分别实例化给vm中的两个变量中
                    vm.user = data.user;
                    //将user的副本赋值
                    vm.user_other = JSON.parse(JSON.stringify(vm.user));
                    
                    if (data.data != "undefined") {
                        //清空缓存数组数据
                        vm.infoListKeys_other.splice(0);
                        vm.infoListValues_other.splice(0);
                        //for in 插入数据
                        for(key in data.data){
                            vm.infoListKeys_other.push(key);
                            vm.infoListValues_other.push(data.data[key]);
                        }
                            
                        vm.dToB(true)
                    }

                    // 头像路径
                    vm.headPath = vm.getHeadPath(data.headPath);
                }).catch(function (err) {
                    console.log("fetch错误" + err);
                    vm.headPath = vm.getHeadPath();
                });
            },

            //获取当前文件的文件名
            getFileName(){
                let str = document.URL;
                return str.slice(str.lastIndexOf("/")+1)
            },

            getHeadPath: function (path) {
                return path === undefined ? "static/image/heads/avater.png" : path;
            },

            //当数据有改动时发送ajax请求，没改动就不会发送，节省资源，减轻服务器压力
            changeAttrs: function () {
                this.addflag = !(this.isDisplay = !this.isDisplay);
                this.btnEdit = this.isDisplay ? "确定" : "编辑";
                //if(changeElement.)
                //this.upHeader(this.isDisplay);
                if (!this.isDisplay) {
                    //如果数组没有改变返回false反之返回true
                    var IsSend = () => {
                        var flag = false;
                        console.log(this.infoListKeys)
                        for (let i = 0; i < this.infoListKeys.length; i++) {
                            if (this.infoListKeys[i] != this.infoListKeys_other[i] ||
                                this.infoListValues[i] != this.infoListValues_other[i]) {;
                                this.infoListKeys_other[i] =  this.infoListKeys[i];
                                this.infoListValues_other[i] = this.infoListValues[i];
                                flag = true;
                            }
                        }
                        for(let el in this.user){
                            if(this.user[el] != this.user_other[el]){
                                this.user_other[el] = this.user[el];
                                flag = true;
                            }
                        }
                        console.log(this.user)
                        console.log(this.user_other)
                        return flag;
                    };
                    // 发送数据
                    if(IsSend()){
                        //生成json字符
                        let jsonString = this.getAttrJSONString(
                            this.infoListKeys_other,
                            this.infoListValues_other
                        );
                        
                        //要发送到服务器的json数据
                        let jsonObj = {
                            user: this.user_other,
                            info: jsonString
                        }
                        //发送ajax请求
                        this.sendAjax("changeUserInfo",jsonObj);

                        
                    }
                    
                    
                }
            },

            upHeader:function(flag){
                if(flag){
                    this.headPath = "static/image/heads/updata.png";
                    this.$refs.head.classList.add("shadow-lg");
                    this.$refs.head.style.cursor = "pointer";
                }else{
                    this.headPath = this.getHeadPath();
                    this.$refs.head.classList.remove("shadow-lg");
                    this.$refs.head.style.cursor = "none";
                }
            },

            deleteAttr: function (sum) {
                // 拷贝数据数组
                this.dToB(false);
                this.infoListKeys_other.splice(sum, 1);
                this.infoListValues_other.splice(sum, 1);
                // 发送数据
                let jsonString = this.getAttrJSONString(
                    this.infoListKeys_other,
                    this.infoListValues_other
                );
                //要发送到服务器的json数据
                let jsonObj = {
                    user: this.user_other,
                    info: jsonString
                }
                this.sendAjax("changeUserInfo",jsonObj);
                //刷新显示数组
                this.dToB(true);
            },

            addAttribute: function () {
                // 拷贝数据数组
                this.dToB(false);
                this.infoListKeys_other.push(this.activeObj.key);
                this.infoListValues_other.push(this.activeObj.value);
                this.activeObj.value = this.activeObj.key = "";
                this.isAddAttr = false;
                this.btnAdd = "添加";
                this.changeflag = !this.isAddAttr;
                // 发送数据
                let jsonString = this.getAttrJSONString(
                    this.infoListKeys_other,
                    this.infoListValues_other
                );
                //要发送到服务器的json数据
                let jsonObj = {
                    user: this.user_other,
                    info: jsonString
                }
                this.sendAjax("changeUserInfo",jsonObj);
                //刷新显示数组
                this.dToB(true);
            },

            addAttributes: function(){
                this.changeflag = !(this.isAddAttr = !this.isAddAttr);
                this.btnAdd = this.isAddAttr ? '取消' : '添加';
                this.activeObj.value = this.activeObj.key = "";
                this.addattrflag = this.isAddAttr;
                this.allRegexError.clear();
            },

            // 将key数组和value数组转为json字符串
            getAttrJSONString: function (keys, values) {
                var jsonString = "";
                for (let i = 0; i < keys.length; i++) {
                    if (i == values.length - 1) {
                        jsonString = jsonString + keys[i] + ":" + values[i];
                    } else {
                        jsonString = jsonString + keys[i] + ":" + values[i] + ",";
                    }
                }
                return (jsonString = "{" + jsonString + "}");
            },

            //将显示数组副本赋值给缓存数组
            //传入的参数为true时缓存副本赋值给显示
            //参数为false时显示副本赋值给缓存
            dToB(flag){
                if(flag){
                    this.infoListKeys = this.infoListKeys_other.concat([]);
                    this.infoListValues = this.infoListValues_other.concat([]);
                }else{
                    this.infoListKeys_other = this.infoListKeys.concat([]);
                    this.infoListValues_other = this.infoListValues.concat([]);
                }
                
            },

            //分辨用户信息是否改变
            isChangeOfUser: function(){

            },

            sendAjax: function (requestPath,jsonObj) {
                console.log(JSON.stringify(jsonObj))
                var ajax = fetch(requestPath, {
                        method: "POST",
                        headers: {
                            "Content-Type": "application/json"
                        },
                        body: JSON.stringify(jsonObj)
                    }).then(function (response) {
                        console.log(response)
                        if (response.status === 200) {
                            return response.json();
                        }
                    }).then(function (data) {
                        vm.data = data;
                    }).catch(function (err) {
                        console.log("fetch错误:" + err);
                    });
            },
            regexInput: function (event, btn) {
                //获取事件触发元素
                var el = event.currentTarget;
                //正则
                var regex = /^[\u4e00-\u9fa5_a-zA-Z0-9]+$/;
                var allError = this.allRegexError;
                if (!regex.test(el.value)) {
                    el.classList.add("text-danger");
                    btn.classList.add("disabled");
                    if (!allError.has(el)) {
                        allError.add(el);
                    }
                    this.addattrflag = this.changeflag = false;
                } else {
                    if (allError.has(el)) {
                        el.classList.remove("text-danger");
                        allError.delete(el);
                        if (allError.size == 0) {
                            btn.classList.remove("disabled");
                            this.addattrflag = this.changeflag =  true;
                        }
                    }
                }
            },

            //跳转到所有用户
            jumpAllUser:function(){
                if(this.user.power == "ADMIN"){
                    // let jsonObj = {
                    //     userPower: this.user.power
                    // }
                    // alert(jsonObj);
                    // this.sendAjax("gotoQuery",jsonObj)
                    window.open("query.html")
                }else{
                    alert("您的权限不够！！！")
                }
            },

            //退出当前账户
            exitLogin:function(){
                var jsonObj = {
                    cookieExpires: 0
                }
                this.sendAjax("exitLogin",jsonObj)
            },

            updataFile(){
                
            }
        },
        // computed: function(){

        // },
        watch: {
            isActive: function (val) {
                if (this.isActive != "") {
                    if (val) {
                        //刷新显示数组
                        this.dToB(true);
                    } else {
                        alert("服务器异常，操作失败");
                    }
                }
                this.isActive = "";
            },
            changeflag:function(val){
                if(!val){
                    this.$refs.changeBtns.classList.add("disabled");
                    
                }else{
                    if(this.infoListKeys.length != 0){
                        this.$refs.changeBtns.classList.remove("disabled");
                    }
                    
                }
            },
            addflag:function(val){
                if(!val){
                    this.$refs.addBtns.classList.add("disabled");
                }else{
                    this.$refs.addBtns.classList.remove("disabled");
                }
            },
            data:function(val){
                if(!!val.jumpAddress){
                    //跳转到该页面
                    window.location.assign(val.jumpAddress)
                }else if(!!val.status){
                    this.isActive = val.status;
                }
            }
        }
    });
};
