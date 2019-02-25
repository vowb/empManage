window.onload=function(){

    // 提交get请求

    // 实例化vue
    var vm = new Vue({
        el:"#all",
        data:{
            users:[],
            delUsers:[],
            pageNumber:1,
            changeNumber:true,
            loadMoreStr: "加载更多"
        },
        created:function(){
            // vue创建后调用get请求方法
            this.getAllList(this.pageNumber)
        },
        methods:{
            addNumber:function(){
                this.pageNumber++
                console.log(this.pageNumber)
            },
        	getAllList:function(page){
                fetch("pageingQuery",{
            		method:"POST",
            		headers:{
            			'Content-Type': 'application/json'
            		},
            		body:JSON.stringify({
            			pageNumber:page
            		})
            	}).then(function (response) {
                    if(response.status === 200){
                        return response.json();
                    }else{
                        alert("获取数据失败")
                    }
                }).then(function (data) {
                    console.log(data)
                    //追加数据到显示视图数组
                    if(data.length == 0){
                        vm.changeNumber = false;
                    }
                	vm.users = vm.users.concat(data)
                }).catch(function (err) {
                    console.log("fetch错误:" + err)
                })
            },
            toPost:function(key,ac){
                console.log(key)
            	// 发送post请求
            	fetch("query",{
            		method:"POST",
            		headers:{
            			'Content-Type': 'application/json'
            		},
            		body:JSON.stringify({
            			active:ac,
            			objID:key
            		})
            	}).then(function(response){
            		console.log(response)
            		if(response.status === 200){
                        return response.json()
                    }else if(response.status === 302){
                        console.log("以跳转")
                    }else{
                        alert("获取数据失败")
                    }
            	}).then(function(data){
                    console.log(data.goToURL != "undefined")
                    if(data.goToURL != "undefined"){
                        //打开新窗口
                        window.location.assign(data.goToURL)
                        //window.open(document.referrer+data.goToURL)
                    }else{
                        vm.delUsers.push(this.users[key])
                    }
                    //vm.delUsers.push(this.users[key])
            	}).catch(function(err){
            		console.log("fetch错误:" + err)
            	})
            	
            	if(ac == "del"){
            		this.users.splice(key,1)
            	}
            }
        },
        filters:{
        	formatTime:function(time){
        		return new Date(time).toLocaleString()
        	}
        },
        watch:{
            pageNumber:function(val){
                //当页码改变时请求服务器
                console.log(val)
                this.getAllList(val)
            },
            changeNumber:function(val){
                this.loadMoreStr = "没有更多了"
            }
        }
    })

}