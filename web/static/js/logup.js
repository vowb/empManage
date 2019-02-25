window.onload = function() {
	// 实例vue
	var vm = new Vue({
		el : "#logup",
		data : {
			againPassTag : "再次输入密码进行验证",
			nameTag : "输入你的真实姓名",
			passTag : "设置你的密码",
			userTag : "设置你的用户名",
			name : "",
			username : "",
			password : "",
			againPassword : "",
			logupUrl : "login.html"
		},
		methods : {
			// 提示信息
			check : function(form, refnameTag, refuserTag, refpassTag,
					refagainpassTag) {

				// 判断是否合法的正则
				var re_username = /^[a-zA-Z0-9]{6,20}$/;
				var re_name = /^[\u4e00-\u9fa5_a-zA-Z0-9]+$/;
				// 判断password是否输入
				if (this.name === "") {
					this.nameTag = "请输入您的真实姓名！！！";
					refnameTag.style.color = "red";
				} else if (!re_name.test(this.name)) {
					this.nameTag = "输入字符不合法 2—10的英文字符或汉字";
					refnameTag.style.color = "red";
				} else {
					if (this.username === "") {
						this.userTag = "请设置用户名！！！";
						refuserTag.style.color = "red";
					} else if (!re_username.test(this.username)) { // 判断username是否和法
						this.userTag = "用户名不合法 6—20的英文字符或数字";
						refuserTag.style.color = "red";
					} else {
						// 判断password是否输入
						if (this.password === "") {

							this.passTag = "请输入密码！！！";
							refpassTag.style.color = "red";
						} else if (this.againPassword === this.password) {
							// 提交表单
							form.submit();
						} else {
							this.againPassTag = "两次输入的密码不相符，重新输入";
							this.againPassword = "";
							refagainpassTag.style.color = "red";
						}
					}
				}
			},
			// 提示信息复原
			focused : function(refTag, event) {
				//console.log(event.target.getAttribute("name"))
				if (event.target.getAttribute("name") === "username") {
					this.userTag = "设置你的用户名";
				} else if (event.target.getAttribute("name") === "password") {
					this.passTag = "设置你的密码";
				} else if (event.target.getAttribute("name") === null) {
					this.againPassTag = "再次输入密码进行验证";
				} else if (event.target.getAttribute("name") === "name") {
					this.nameTag = "输入你的真实姓名";
				}
				refTag.style.color = "#333";
			},
			// 注册跳转链接
			jump : function() {
				window.location.assign(this.logupUrl);
			}
		},
		watch:{
			name:function(newValue){
				var re = /[\s]*/g
				this.name = newValue.replace(re,"")
			}
		}
	});
}