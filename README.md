这个是一个懒人用于idea的插件，目前有两个功能
1.自动生成测试用例

如果你的代码是这样的:

        @MonitorSwagger  
        @RestController  
        @RequestMapping(value = SOME_STRING + "/company")  
        public class TestController {  
          @ApiResponses(value = {   
          @ApiResponse(code = 404, message = "error")   
          })   
          @PostMapping("/xxxx/xx")   
          public ResponseEntity<Object> filter(@ApiParam(value = "搜索过滤参数", required = true) @RequestBody Object request) throws SuspendExecution {   
            return new ResponseEntity<>(response, HttpStatus.OK);  
          }  
        }  
        你将会在剪切办得到这样一个东西：   
      	/**  
        * error   
        */   
        @Test   
        public void filter_404() {   
            def headers = userLogin(accountInfo.dj)   
            def body = [  
                "request" : "Object"  
            ]   
            def res = restClient().post(  
                path: "/api/company/xxxx/xx",   
                contentType: JSON,   
                body : body,   
                headers: headers)   
            assert res.status == 404202   
        }
        ps : def restClient = new RESTClient("127.0.0.1:8080")
        如果你的Object是一个其他实体,我将会把所有字段都对应读取出来
2.Simple Plugin (位于上边栏最后一个):
step1: 拥有一台linux系统的电脑
step2: 安装expect
step3: 安装terminator
step4: 配置config, 配置文件 在.IntelliJIdea2016.3/config/plugins/bb中
config 格式 : 中间用空格隔开

别名 帐号 密码 ip 注释

别名最好用英文，支持中文。如果没有密码，用？代替。当通过这个插件登录过一次远程服务器。系统自动加入别名，可以直接用别名登录。
效果类似： ssh username@ip ,自动帮助你输入密码，完成登录。
