// 全局基础配置
const baseURL = '';

/**
 * 通用AJAX请求封装
 * @param {string} url
 * @param {string} method
 * @param {object|null} data
 */
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

/**
 * 获取URL查询参数
 * @param {string} name 参数名
 * @returns {string|null}
 */
function getQueryParam(name) {
    const reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)");
    const r = window.location.search.substr(1).match(reg);
    if (r != null) return unescape(r[2]);
    return null;
}

/**
 * 渲染顶部导航栏用户状态（V2新增我的订单、我的课程菜单）
 */
function renderNav() {
    ajaxRequest('/api/user/info', 'GET').done(function (res) {
        const userNav = $('#userNav');
        if (res.code === 200) {
            const user = res.data;
            let html = `<span class="nav-link">欢迎，${user.username}</span>
                        <a class="btn btn-outline-light ms-1" href="/my-order.html">我的订单</a>
                        <a class="btn btn-outline-light ms-1" href="/my-course.html">我的课程</a>
                        <button class="btn btn-outline-light ms-2" id="logoutBtn">退出</button>`;
            // 管理员额外展示后台入口
            if (user.role === 1) {
                html += `<a class="btn btn-outline-light ms-1" href="/admin/course.html">后台管理</a>`;
            }
            userNav.html(html);

            // 退出登录事件
            $('#logoutBtn').click(function () {
                ajaxRequest('/api/user/logout', 'POST').done(function () {
                    window.location.href = '/index.html';
                });
            });
        } else {
            // 未登录状态
            userNav.html(`<a class="btn btn-outline-light" href="/login.html">登录/注册</a>`);
        }
    });
}