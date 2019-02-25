window.onload=function(){
    //实例vue
    var vm = new Vue({
        el:"#login",
        data:{
            passTag: "输入你的密码",
            userTag: "输入你的用户名",
            username: "",
            password: "",
            logupUrl: "logup.html"
        },
        methods:{
            //提示信息
            check:function(form,refuserTag,refpassTag){

                //判断是否合法的正则
                var re = /^[a-zA-Z0-9]{6,20}$/;
                //判断password是否输入
                if(this.username === ""){
                    this.userTag = "请输入用户名！！！";
                    refuserTag.style.color = "red";
                } else if (!re.test(this.username)) {    //判断username是否和法
                    this.userTag = "用户名是6—20的英文字符或数字";
                    refuserTag.style.color = "red";
                }else{
                    //判断password是否输入
                    if (this.password === "") {

                        this.passTag = "请输入密码！！！";
                        refpassTag.style.color = "red";
                    } else {
                        //提交表单
                        form.submit();
                    }
                }
            },
            //提示信息复原
            focused: function (refTag,event){
                if(event.target.getAttribute("name") === "username"){
                    this.userTag = "输入你的用户名";
                } else if (event.target.getAttribute("name") === "password"){
                    this.passTag = "输入你的密码";
                }
                refTag.style.color = "#333";
            },
            //注册跳转链接
            jump:function(){
                window.location.assign(this.logupUrl);
            }
        }
    });
}