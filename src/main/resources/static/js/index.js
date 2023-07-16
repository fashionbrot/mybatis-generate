let keyPrefix = "mg";
$(function () {
    loadDatabase();
    loadData();

    $("#searchButton").click(function () {
        loadData();
    });

    $("#all").click(function () {
        if (this.checked) {
            $("input[name='checkboxName']").prop("checked", true);
        } else {
            $("input[name='checkboxName']").prop("checked", false);
        }
    });


    eventInput(new Array("#responseClassName","#controllerSuffix","#serviceSuffix","#serviceImplSuffix","#mapperSuffix","#mapperXmlSuffix","#entitySuffix","#requestSuffix","#responseSuffix"),function (obj){
        let value=$(obj).val();
        let newValue = firstToUpperCase(value);
        $(obj).val(newValue);
    });

    loadRequestSetCookie();

    $("#databaseId").on("change",function (){
        loadRequestSetCookie();
    });



    checkBoxClick(new Array("#swagger2Enable",
        "#swagger3Enable",
        "#pageHelperEnable",
        "#mapperXmlAliasEnable",
        "#mapperXmlInsertsEnable",
        "#mapperXmlInsertEnable",
        "#mapperXmlUpdateByIdEnable",
        "#mapperXmlDeleteByIdEnable",
        "#mapperXmlSelectByIdEnable",
        "#customPageListInterfaceEnable",
        "#customSelectByIdInterfaceEnable",
        "#customDeleteByIdsInterfaceEnable",
        "#customDeleteByIdInterfaceEnable",
        "#customUpdateByIdInterfaceEnable",
        "#customInsertInterfaceEnable",
        "#customListInterfaceEnable",
        "#customPageListInterfaceEnable"
    ),function (obj){
        inputCheckbox(obj)
    });

    $(function () { $("[data-toggle='tooltip']").tooltip(); });
});

$("#databaseConfig").on("click", function () {
    loadDatabaseShowTable();
    $('#myModal').modal('show')
})

$("#clearButton").on("click", function () {
    $("#tableName").val('');
    loadData();
});

function inputCheckbox(obj){
    let checked = $(obj).is(':checked');
    if (checked){
        $(obj).val('true');
    }else{
        $(obj).val('false');
    }
}






function loadData() {
    $.ajax({
        url: ctx + "list",
        type: "post",
        timeout:3000,
        data: {"tableName": $("#tableName").val(), "databaseName": $("#databaseId").val()},
        dataType: "json",
        beforeSend: function () {
            $("#searchButton").button('loading');
        },
        complete: function () {
            $("#searchButton").button('complete');
        },
        error: function () {
            $("#searchButton").button('complete');
        },
        success: function (data) {
            if (data.code == 0) {
                var rows = data.data;
                if (rows) {
                    var h = "";
                    for (var i = 0; i < rows.length; i++) {
                        var li = rows[i];
                        h += "<tr>";
                        h += "<td>";
                        h += "<input type='checkbox' name='checkboxName' value='" + li.tableName + "'>"
                        h += "</td>";
                        h += "<td>";
                        h += i + 1;
                        h += "</td>";
                        h += "<td>";
                        h += li.tableName;
                        h += "</td>";
                        h += "<td>";
                        h += li.comments
                        h += "</td>";
                        h += "<td>";
                        h += li.createTime
                        h += "</td>";
                        h += "</tr>";
                    }
                    $("#tableBody").html(h);
                }
            } else {
                Toast(data.msg);
            }
        }
    });
}

function generateCode() {

    var obj = document.getElementsByName("checkboxName");
    var check_val = [];
    for (k in obj) {
        if (obj[k].checked) {
            check_val.push(obj[k].value);
        }
    }
    if (check_val.length <= 0) {
        Toast("请选择以下表");
        return false;
    }

    var data = $("#formDiv").serialize() + "&selectTableNames=" + check_val.join(",") + "&" + $("#left").serialize()+"&databaseName="+$("#databaseId").val();
    var outType = $("#outType").val();
    if (outType=="zip"){
        var url = ctx + "generateZip";
        $.download(url,data,'post' );
    }else{

        var r = confirm("生成到本地会覆盖代码，确定要执行吗?")
        if (r) {
            $.ajax({
                url: ctx + "generate",
                type: "post",
                timeout: 2000,
                data: data,
                dataType: "json",
                beforeSend: function () {
                    $("#generate").button('loading');
                },
                complete: function () {
                    $("#generate").button('complete');
                },
                error: function () {
                    $("#generate").button('complete');
                },
                success: function (data) {
                    if (data.code == 0) {
                        Toast("生成成功");
                    } else {
                        alert(data.msg);
                    }
                }
            });
        }

    }
}

jQuery.download = function(url, data, method){ // 获得url和data
    if( url && data ){
        // data 是 string 或者 array/object
        data = typeof data == 'string' ? data : jQuery.param(data); // 把参数组装成 form的 input
        var inputs = '';
        jQuery.each(data.split('&'), function(){
            var pair = this.split('=');
            inputs+='<input type="hidden" name="'+ pair[0] +'" value="'+ pair[1] +'" />';
        }); // request发送请求
        jQuery('<form action="'+ url +'" target="_blank" method="'+ (method||'post') +'">'+inputs+'</form>').appendTo('body').submit().remove();
    };
};


function jinyong(obj) {
    let inputs = $(obj).parent().parent().find("input");
    if (inputs && inputs.length>1){
        for (let i = 0; i < inputs.length; i++) {
            let input = $(inputs[i]);
            if (!input.attr("disabled")) {
                input.attr("disabled", "disabled")
                $(obj).html("<span style='color:#07c160;'>启用</span>")
            } else {
                input.removeAttr("disabled")
                $(obj).html("禁用")
            }
        }
    }
}


function loadDatabase() {
    $.ajax({
        url: ctx + "load",
        type: "post",
        dataType: "json",
        async: false,
        success: function (data) {
            if (data.code == 0) {
                var rows = data.data;
                if (rows) {
                    var html = "";
                    for (var i = 0; i < rows.length; i++) {
                        var row = rows[i];
                        html += "<option id='" + row.name + "'>" + row.name + "</option>";
                    }
                    $("#databaseId").html(html);
                }
            } else {
                alert(data.msg);
            }
        }
    });
}

function loadDatabaseShowTable() {
    $.ajax({
        url: ctx + "load",
        type: "post",
        dataType: "json",
        async: false,
        success: function (data) {
            if (data.code == 0) {
                var rows = data.data;
                if (rows) {
                    var html = "";
                    for (var i = 0; i < rows.length; i++) {
                        var row = rows[i];

                        var url= row.url;
                        if (url){
                            url = row.url.replaceAll("?","<br/>").replaceAll("&","<br/>")
                        }

                        html += "<tr>";
                        html += "<td class='name'>" + row.name + "</td>";
                        html += "<td class='driverClassName'>" + row.driverClassName + "</td>";
                        html += "<td class='url' value='"+row.url+"' style='width:200px;'>" + url + "</td>";
                        html += "<td class='username'>" + row.username + "</td>";
                        html += "<td class='password'>" + row.password + "</td>";
                        html += "<td> <a type=\"button\" class=\"btn btn-xs\"  onclick=\"deleteDatabase('" + row.name + "')\" >删除</a> <a type=\"button\" class=\"btn btn-xs\"  onclick=\"updateDatabase(this)\" >修改</a></td>";
                        html += "</tr>";
                    }
                    $("#databaseTableBody").html(html);
                }
            } else {
                alert(data.msg);
            }
        }
    });
}

function deleteDatabase(name) {
    var r = confirm("确定要删除吗?")
    if (r) {
        $.ajax({
            url: ctx + "remove",
            type: "post",
            data: {"name": name},
            dataType: "json",
            async: false,
            success: function (data) {
                if (data.code == 0) {
                    loadDatabaseShowTable();
                    loadDatabase();
                    deleteLocalStorageDatabaseId(name);
                } else {
                    alert(data.msg);
                }
            }
        });
    }
}

function addDatabase() {
    $.ajax({
        url: ctx + "reload",
        type: "post",
        data: $("#addForm").serialize(),
        dataType: "json",
        async: false,
        success: function (data) {
            if (data.code == 0) {
                loadDatabase();
                loadDatabaseShowTable();
                $("#add").modal("hide");
            } else {
                alert(data.msg);
            }
        }
    });
}

function updateDatabase(obj) {
    var parent = $(obj).parent().parent();
    $("#name").val(parent.find(".name").html());
    $("#driverClassName").val(parent.find(".driverClassName").html());
    $("#url").val(HTMLDecode(parent.find(".url").attr("value")));
    $("#password").val(parent.find(".password").html());
    $("#username").val(parent.find(".username").html());
    $("#add").modal("show");
}

function HTMLDecode(text) {
    var temp = document.createElement("div");
    temp.innerHTML = text;
    var output = temp.innerText || temp.textContent;
    temp = null;
    return output;
}

function isEmpty(outValue) {
    if (outValue != null && outValue != '' && outValue != 'undefined' && outValue != undefined) {
        return false;
    }
    return true;
}

String.prototype.endWith = function (endStr) {
    var d = this.length - endStr.length;
    return (d >= 0 && this.lastIndexOf(endStr) == d);
}

function Toast(msg,duration){
    duration=isNaN(duration)?3000:duration;
    var m = document.createElement('div');
    m.innerHTML = msg;
    m.style.cssText="max-width:60%;min-width: 150px;padding:0 14px;height: 40px;color: rgb(255, 255, 255);line-height: 40px;text-align: center;border-radius: 4px;position: fixed;top: 50%;left: 50%;transform: translate(-50%, -50%);z-index: 999999;background: rgba(0, 0, 0,.7);font-size: 16px;";
    document.body.appendChild(m);
    setTimeout(function() {
        var d = 0.5;
        m.style.webkitTransition = '-webkit-transform ' + d + 's ease-in, opacity ' + d + 's ease-in';
        m.style.opacity = '0';
        setTimeout(function() { document.body.removeChild(m) }, d * 1000);
    }, duration);
}

function eventChange(selectElement,callback) {
    if (selectElement instanceof Array){
        for (var i=0;i<selectElement.length;i++) {
            $(selectElement[i]).on("change", function () {
                if (typeof callback == "function") {
                    callback(this);
                }
            })
        }
    }else{
        $(selectElement).on("change",function () {
            if (typeof callback == "function") {
                callback(this);
            }
        })
    }

}
function eventInput(selectElement,callback) {
    if (selectElement instanceof Array){
        for (var i=0;i<selectElement.length;i++){
            $(selectElement[i]).bind('input propertychange', function () {
                if (typeof callback == "function") {
                    callback(this);
                }
            })
        }
    }else{
        $(selectElement).bind('input propertychange', function () {
            if (typeof callback == "function") {
                callback(this);
            }
        })
    }
}

function checkBoxClick(selectElement,callback) {
    if (selectElement instanceof Array){
        for (var i=0;i<selectElement.length;i++){
            $(selectElement[i]).on("change", function () {
                if (typeof callback == "function") {
                    callback(this);
                }
            })
        }
    }else{
        $(selectElement).on("change", function () {
            if (typeof callback == "function") {
                callback(this);
            }
        })
    }
}

function firstToUpperCase(name){
    if (name){
        return name.charAt(0).toUpperCase() + name.slice(1)
    }
    return "";
}

function saveRequestSetCookie(){
    let databaseId =$("#databaseId").val();
    if (databaseId){
        databaseId = keyPrefix+md5(databaseId);
        setLocalStorage(databaseId,"out");
        setLocalStorage(databaseId,"packageOut")

        setLocalStorage(databaseId,"controllerOut")
        setLocalStorage(databaseId,"controllerSuffix")

        setLocalStorage(databaseId,"serviceOut")
        setLocalStorage(databaseId,"serviceSuffix")

        setLocalStorage(databaseId,"serviceImplOut")
        setLocalStorage(databaseId,"serviceImplSuffix")

        setLocalStorage(databaseId,"mapperOut")
        setLocalStorage(databaseId,"mapperSuffix")

        setLocalStorage(databaseId,"entityOut")
        setLocalStorage(databaseId,"entitySuffix")

        setLocalStorage(databaseId,"requestOut")
        setLocalStorage(databaseId,"requestSuffix")

        setLocalStorage(databaseId,"responseOut")
        setLocalStorage(databaseId,"responseSuffix")

        setLocalStorage(databaseId,"responseClassName")

        setLocalStorage(databaseId,"permissionOut")
        setLocalStorage(databaseId,"permissionClassName")
        setLocalStorage(databaseId,"permissionEnable")



        setLocalStorage(databaseId,"author")

        setLocalStorage(databaseId,"swagger2Enable");
        setLocalStorage(databaseId,"swagger3Enable");
        setLocalStorage(databaseId,"pageHelperEnable");
        setLocalStorage(databaseId,"mapperXmlAliasEnable");
        setLocalStorage(databaseId,"customPageListInterfaceEnable")
    }
    Toast("缓存成功");
}
function loadRequestSetCookie(){
    let databaseId =$("#databaseId").val();
    if (databaseId){
        databaseId = keyPrefix+md5(databaseId);
        loadLocalStorage(databaseId,"out");
        loadLocalStorage(databaseId,"packageOut")

        loadLocalStorage(databaseId,"controllerOut")
        loadLocalStorage(databaseId,"controllerSuffix")

        loadLocalStorage(databaseId,"serviceOut")
        loadLocalStorage(databaseId,"serviceSuffix")

        loadLocalStorage(databaseId,"serviceImplOut")
        loadLocalStorage(databaseId,"serviceImplSuffix")

        loadLocalStorage(databaseId,"mapperOut")
        loadLocalStorage(databaseId,"mapperSuffix")

        loadLocalStorage(databaseId,"entityOut")
        loadLocalStorage(databaseId,"entitySuffix")

        loadLocalStorage(databaseId,"requestOut")
        loadLocalStorage(databaseId,"requestSuffix")

        loadLocalStorage(databaseId,"responseOut")
        loadLocalStorage(databaseId,"responseSuffix")

        loadLocalStorage(databaseId,"responseClassName")

        loadLocalStorage(databaseId,"permissionOut")
        loadLocalStorage(databaseId,"permissionClassName")
        loadLocalStorage(databaseId,"permissionEnable")

        loadLocalStorage(databaseId,"author")


        loadLocalStorage(databaseId,"swagger2Enable");
        loadLocalStorage(databaseId,"swagger3Enable");
        loadLocalStorage(databaseId,"pageHelperEnable");
        loadLocalStorage(databaseId,"mapperXmlAliasEnable");
        loadLocalStorage(databaseId,"customPageListInterfaceEnable")
    }
}


function setLocalStorage(key,inputId){
    let inputType = document.getElementById(inputId).type;
    if (inputType=="checkbox"){
        let checked = $('#'+inputId).is(':checked');
        localStorage.setItem(key+"_"+inputId, checked);
    }else if (inputType=="text"){
        localStorage.setItem(key+"_"+inputId, $("#"+inputId).val());
    }

}
function loadLocalStorage(key,inputId){

    let inputType = document.getElementById(inputId).type;
    if (inputType=="checkbox"){
        let value = localStorage.getItem(key+"_"+inputId);
        if ("true"==value){
            $('#'+inputId).prop('checked',true);
        }else if ("false"==value){
            $('#'+inputId).prop('checked',false);
        }
    }else if (inputType=="text"){
        let value = localStorage.getItem(key+"_"+inputId);
        if (value){
            $("#"+inputId).val(value);
        }
    }

}

function deleteLocalStorageDatabaseId(databaseId) {
    let deleteKeyPrefix = keyPrefix+md5(databaseId);
    for(var i=0;i<localStorage.length;i++){
        let key = localStorage.key(i);
        if (key.startsWith(deleteKeyPrefix)){
            localStorage.removeItem(key);
        }
    }
}