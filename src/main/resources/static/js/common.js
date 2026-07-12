// 全局基础配置
const baseURL = '';

// 通用AJAX请求封装
function ajaxRequest(url, method = 'GET', data = null) {
    return $.ajax({
        url: baseURL + url,
        type: method,
        contentType: 'application/json',
        data: data ? JSON.stringify(data) : null,
        xhrFields: {
            withCredentials: true
        },
        error: function (xhr) {
            if (xhr.status === 401) {
                alert('请先登录');
                window.location.href = '/login.html';
            } else if (xhr.responseJSON && xhr.responseJSON.msg) {
                alert(xhr.responseJSON.msg);
            } else {
                alert('请求失败，请稍后重试');
            }
        }
    });
}

// 获取URL参数
function getQueryParam(name) {
    const reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)");
    const r = window.location.search.substr(1).match(reg);
    if (r != null) return unescape(r[2]);
    return null;
}

// 渲染顶部导航栏用户状态
function renderNav() {
    ajaxRequest('/api/user/info', 'GET').done(function (res) {
        const userNav = $('#userNav');
        if (res.code === 200) {
            const user = res.data;
            let html = `<span class="nav-link">欢迎，${user.username}</span>
                        <button class="btn btn-outline-light ms-2" id="logoutBtn">退出</button>`;
            // 管理员显示后台入口
            if (user.role === 1) {
                html += `<a class="btn btn-outline-light ms-2" href="/admin/course.html">后台管理</a>`;
            }
            userNav.html(html);

            $('#logoutBtn').click(function () {
                ajaxRequest('/api/user/logout', 'POST').done(function () {
                    window.location.reload();
                });
            });
        } else {
            userNav.html(`<a class="btn btn-outline-light" href="/login.html">登录/注册</a>`);
        }
    });
}