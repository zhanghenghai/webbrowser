$(function () {
    /**
    * 存储获取数据函数
    * @function get 存储数据
    * @function set 获取数据
    */
    var store = {
        /**
         * 存储名称为key的val数据
         * @param {String} key 键值
         * @param {String} val 数据
         */
        set: function (key, val) {
            if (!val) {
                return;
            }
            try {
                var json = JSON.stringify(val);
                if (typeof JSON.parse(json) === "object") { // 验证一下是否为JSON字符串防止保存错误
                    localStorage.setItem(key, json);
                }
            } catch (e) {
                return false;
            }
        },
        /**
         * 获取名称为key的数据
         * @param {String} key 键值
         */
        get: function (key) {
            if (this.has(key)) {
                return JSON.parse(localStorage.getItem(key));
            }
        },
        has: function (key) {
            if (localStorage.getItem(key)) {
                return true;
            } else {
                return false;
            }
        },
        del: function (key) {
            localStorage.removeItem(key);
        }
    };

    // 第一页数据
    var oneData = {
        "one": [
            {
                "hl": "ONE",
                "shl": "韩寒监制",
                "img": "bilibilibog",
                "url": "m.wufazhuce.com"
            },
            {
                "hl": "蚂蜂窝",
                "shl": "旅游攻略社区",
                "img": "discover",
                "url": "m.mafengwo.cn"
            },
            {
                "hl": "小红书",
                "shl": "可以买到国外的好东西",
                "img": "ithome",
                "url": "www.xiaohongshu.com"
            },
            {
                "hl": "什么值得买",
                "shl": "应该能省点钱吧",
                "img": "netease",
                "url": "m.smzdm.com"
            },
            {
                "hl": "淘票票",
                "shl": "不看书，就看几场电影吧",
                "img": "taobao",
                "url": "dianying.taobao.com"
            }
        ]
    };
    // 第一页数据

    // 第一页代码拼接
    oneHtml = '<div style="min-height: 70px;flex: 1;display: flex;flex-wrap:wrap">';
    $.each(oneData, function (i, n) {
        for (var i = 0, l = n.length; i < l; i++) {
            oneHtml += '<div style="height:100%;flex: 0 0 25%;margin-bottom:5%">' + '<div class="img" style="background-image: url(image/icon/' + n[i].img + '.png")"></div>' + '<div class="text">哔哩哔哩</div>' + '</div>';
        }
        oneHtml += '</div>';
    });
    $('#one').append(oneHtml);
    // 第一页代码拼接

    // 对象
    var bookMarkFn = function (options) {
        this.options = {
            data: [
                {
                    "id": 1,
                    "hl": "ONE",
                    "shl": "韩寒监制",
                    "img": "bilibilibog",
                    "url": "m.wufazhuce.com"
                },
                {
                    "id": 2,
                    "hl": "蚂蜂窝",
                    "shl": "旅游攻略社区",
                    "img": "discover",
                    "url": "m.mafengwo.cn"
                },
                {
                    "id": 3,
                    "hl": "小红书",
                    "shl": "可以买到国外的好东西",
                    "img": "ithome",
                    "url": "www.xiaohongshu.com"
                },
                {
                    "id": 4,
                    "hl": "什么值得买",
                    "shl": "应该能省点钱吧",
                    "img": "netease",
                    "url": "m.smzdm.com"
                },
                {
                    "id": 5,
                    "hl": "淘票票",
                    "shl": "不看书，就看几场电影吧",
                    "img": "taobao",
                    "url": "dianying.taobao.com"
                },
                {
                    "id": 6,
                    "hl": "淘票票",
                    "shl": "不看书，就看几场电影吧",
                    "img": "taobao",
                    "url": "dianying.taobao.com"
                },
                {
                    "id": 7,
                    "hl": "淘票票",
                    "shl": "不看书，就看几场电影吧",
                    "img": "taobao",
                    "url": "dianying.taobao.com"
                },
                {
                    "id": 8,
                    "hl": "淘票票",
                    "shl": "不看书，就看几场电影吧",
                    "img": "taobao",
                    "url": "dianying.taobao.com"
                },
                {
                    "id": 9,
                    "hl": "淘票票",
                    "shl": "不看书，就看几场电影吧",
                    "img": "taobao",
                    "url": "dianying.taobao.com"
                },
                {
                    "id": 10,
                    "hl": "淘票票",
                    "shl": "不看书，就看几场电影吧",
                    "img": "taobao",
                    "url": "dianying.taobao.com"
                },
                {
                    "id": 11,
                    "hl": "淘票票",
                    "shl": "不看书，就看几场电影吧",
                    "img": "taobao",
                    "url": "dianying.taobao.com"
                },
                {
                    "id": 12,
                    "hl": "淘票票",
                    "shl": "不看书，就看几场电影吧",
                    "img": "taobao",
                    "url": "dianying.taobao.com"
                },
                {
                    "id": 13,
                    "hl": "淘票票",
                    "shl": "不看书，就看几场电影吧",
                    "img": "taobao",
                    "url": "dianying.taobao.com"
                },
                {
                    "id": 14,
                    "hl": "淘票票",
                    "shl": "不看书，就看几场电影吧",
                    "img": "taobao",
                    "url": "dianying.taobao.com"
                },
                {
                    "id": 15,
                    "hl": "淘票票",
                    "shl": "不看书，就看几场电影吧",
                    "img": "taobao",
                    "url": "dianying.taobao.com"
                },
                {
                    "id": 16,
                    "hl": "淘票票",
                    "shl": "不看书，就看几场电影吧",
                    "img": "taobao",
                    "url": "dianying.taobao.com"
                },
                {
                    "id": 17,
                    "hl": "淘票票",
                    "shl": "不看书，就看几场电影吧",
                    "img": "taobao",
                    "url": "dianying.taobao.com"
                },
                {
                    "id": 18,
                    "hl": "淘票票",
                    "shl": "不看书，就看几场电影吧",
                    "img": "taobao",
                    "url": "dianying.taobao.com"
                },
                {
                    "id": 19,
                    "hl": "淘票票",
                    "shl": "不看书，就看几场电影吧",
                    "img": "taobao",
                    "url": "dianying.taobao.com"
                },
                {
                    "id": 20,
                    "hl": "淘票票",
                    "shl": "不看书，就看几场电影吧",
                    "img": "taobao",
                    "url": "dianying.taobao.com"
                },
                {
                    "id": 21,
                    "hl": "淘票票",
                    "shl": "不看书，就看几场电影吧",
                    "img": "taobao",
                    "url": "dianying.taobao.com"
                },
                {
                    "id": 22,
                    "hl": "淘票票",
                    "shl": "不看书，就看几场电影吧",
                    "img": "taobao",
                    "url": "dianying.taobao.com"
                },
                {
                    "id": 23,
                    "hl": "淘票票",
                    "shl": "不看书，就看几场电影吧",
                    "img": "taobao",
                    "url": "dianying.taobao.com"
                },
                {
                    "id": 24,
                    "hl": "淘票票",
                    "shl": "不看书，就看几场电影吧",
                    "img": "taobao",
                    "url": "dianying.taobao.com"
                },
                {
                    "id": 24,
                    "hl": "淘票票",
                    "shl": "不看书，就看几场电影吧",
                    "img": "taobao",
                    "url": "dianying.taobao.com"
                },
                {
                    "id": 25,
                    "hl": "淘票票",
                    "shl": "不看书，就看几场电影吧",
                    "img": "taobao",
                    "url": "dianying.taobao.com"
                },
                {
                    "id": 26,
                    "hl": "淘票票",
                    "shl": "不看书，就看几场电影吧",
                    "img": "taobao",
                    "url": "dianying.taobao.com"
                },
                {
                    "id": 27,
                    "hl": "淘票票",
                    "shl": "不看书，就看几场电影吧",
                    "img": "taobao",
                    "url": "dianying.taobao.com"
                },
                {
                    "id": 28,
                    "hl": "淘票票",
                    "shl": "不看书，就看几场电影吧",
                    "img": "taobao",
                    "url": "dianying.taobao.com"
                },
                {
                    "id": 29,
                    "hl": "淘票票",
                    "shl": "不看书，就看几场电影吧",
                    "img": "taobao",
                    "url": "dianying.taobao.com"
                },
                {
                    "id": 30,
                    "hl": "淘票票",
                    "shl": "不看书，就看几场电影吧",
                    "img": "taobao",
                    "url": "dianying.taobao.com"
                },
                {
                    "id": 31,
                    "hl": "淘票票",
                    "shl": "不看书，就看几场电影吧",
                    "img": "taobao",
                    "url": "dianying.taobao.com"
                },
                {
                    "id": 32,
                    "hl": "淘票票",
                    "shl": "不看书，就看几场电影吧",
                    "img": "taobao",
                    "url": "dianying.taobao.com"
                },
                {
                    "id": 33,
                    "hl": "淘票票",
                    "shl": "不看书，就看几场电影吧",
                    "img": "taobao",
                    "url": "dianying.taobao.com"
                },
                {
                    "id": 34,
                    "hl": "淘票票",
                    "shl": "不看书，就看几场电影吧",
                    "img": "taobao",
                    "url": "dianying.taobao.com"
                },
                {
                    "id": 35,
                    "hl": "淘票票",
                    "shl": "不看书，就看几场电影吧",
                    "img": "taobao",
                    "url": "dianying.taobao.com"
                },
                {
                    "id": 36,
                    "hl": "淘票票",
                    "shl": "不看书，就看几场电影吧",
                    "img": "taobao",
                    "url": "dianying.taobao.com"
                },
                {
                    "id": 37,
                    "hl": "淘票票",
                    "shl": "不看书，就看几场电影吧",
                    "img": "taobao",
                    "url": "dianying.taobao.com"
                },
                {
                    "id": 38,
                    "hl": "淘票票",
                    "shl": "不看书，就看几场电影吧",
                    "img": "taobao",
                    "url": "dianying.taobao.com"
                },
                {
                    "id": 39,
                    "hl": "淘票票",
                    "shl": "不看书，就看几场电影吧",
                    "img": "taobao",
                    "url": "dianying.taobao.com"
                },
                {
                    "id": 40,
                    "hl": "淘票票",
                    "shl": "不看书，就看几场电影吧",
                    "img": "taobao",
                    "url": "dianying.taobao.com"
                },
                {
                    "id": 41,
                    "hl": "淘票票",
                    "shl": "不看书，就看几场电影吧",
                    "img": "taobao",
                    "url": "dianying.taobao.com"
                },
                {
                    "id": 42,
                    "hl": "淘票票",
                    "shl": "不看书，就看几场电影吧",
                    "img": "taobao",
                    "url": "dianying.taobao.com"
                },
                {
                    "id": 43,
                    "hl": "淘票票",
                    "shl": "不看书，就看几场电影吧",
                    "img": "taobao",
                    "url": "dianying.taobao.com"
                },
                {
                    "id": 44,
                    "hl": "淘票票",
                    "shl": "不看书，就看几场电影吧",
                    "img": "taobao",
                    "url": "dianying.taobao.com"
                },
                {
                    "id": 45,
                    "hl": "淘票票",
                    "shl": "不看书，就看几场电影吧",
                    "img": "taobao",
                    "url": "dianying.taobao.com"
                },
            ]
        };
        // 合并对象 将本地数据this.options 与 bookMark 对象进行合并
        this.options = $.extend({}, this.options, options);
        this.init();
    }

    bookMarkFn.prototype = {
        // 初始化
        init: function () {
            var html = '';
            var data = this.options.data;
            console.log("获取长度 >>> " + data.length);
            // 将数据总数和每组数据个数存储在变量中
            const totalCount = data.length;
            const groupSize = 20;
            // 计算组数并向上取整
            const numGroups = Math.ceil(totalCount / groupSize);
            console.log("需要的组数是：" + numGroups);
            // 判断需要创建多少个页面
            let pageHtml;
            for (var i = 0; i < numGroups; i++) {
                pageHtml = '<div class="swiper-slide">' + '<div style="width: 100%;height: 100%;position: relative;flex-direction: column;text-align: center;" id="page' + i + '"></div></div>'
                console.log("循环次数" + i)
                // 页面内容进行分配
                dataHtml = '<div class="parent">';
                // 使用分页进行显示
                // 一页显示多少个数据
                const pageSize = groupSize;
                // 第几页
                const currentPage = i + 1;
                console.log("当前页数 >>> " + currentPage)
                // 进行计算
                const startIndex = (currentPage - 1) * pageSize;
                const endIndex = startIndex + pageSize;
                // 获取到数据
                let currentPageData = data.slice(startIndex, endIndex);
                console.log(currentPageData);

                // 计时器
                let timerId;

                for (var x = 0; x < currentPageData.length; x++) {
                    // 分页循环
                    console.log("APP >>> " + currentPageData[x].id);
                    var id = currentPageData[x].id
                    var url = currentPageData[x].url
                    dataHtml += '<div class="child list' + i + '" id="app' + id + '"' + ' data-id="' + id + '" data-url="' + url + '" style="height:100%;margin-bottom:5%">' + '<div style=""><div class="img" style="background-image: url(image/icon/' + currentPageData[x].img + '.png")"></div><div class="text">' + currentPageData[x].hl + '</div></div></div>';




                    $(document).click(function () {
                        console.log('触发了')
                        $(".delbook").removeClass();
                    });


                    //长按事件
                    // $(document).on('touchstart', '#app' + currentPageData[x].id, function (e) {
                    //     var elementId = $(this).attr('id'); // 获取控件的 ID
                    //     var data_id = $(this).attr('data_id'); // 获取数据ID
                    //     // 开始长按的代码
                    //     timeout = setTimeout(function () {
                    //         // 点击触发事件
                    //         openModal()
                    //         // 长按事件的代码
                    //         // $("#" + elementId).find(".img").prepend('<div class="delbook" id="myOn' + elementId + '"></div>');
                    //         // 长按持续时间
                    //     }, 700);
                    // }).on('touchend', '#app' + currentPageData[x].id, function (e) {
                    //     clearTimeout(timeout);
                    // });




                }
                dataHtml += '</div>';

                $('#sw').append(pageHtml);
                //$('#page' + i).append(dataHtml);
                this.$ele = $('#page' + i);
                console.log("输出 >>>> " + dataHtml)
                // 显示页面
                this.$ele.html(dataHtml);

                console.log()
                // 控件綁定
                this.bind(i);
            }

        },

        bind: function (i) {
            console.log('获取当前类 >>> ' + i)
            var that = this;
            var data = this.options.data;
            // 计时器
            let timerId;


            // 长按事件
            // this.$ele.longPress(function (evt) {
            //     evt.stopPropagation();
            //     var dom = $(evt.currentTarget);
            //     var id = dom.data("id");
            //     // 删除数据
            //     const index = findIndexByid(id, data);
            //     console.log('下标 >>>> ' + index);
            //     openModal(data,index)
            // });

            //长按事件
            this.$ele.on('touchstart', '.list' + i, function (evt) {
                // 开始长按的代码
                timeout = setTimeout(function () {
                    var dom = $(evt.currentTarget);
                    var id = dom.data("id");
                    const index = findIndexByid(id, data);
                    openModal(dom,evt,store,data,index)
                }, 700);
            }).on('touchend', '.list' + i, function (e) {
                clearTimeout(timeout);
            });



            // 点击事件
            this.$ele.on('click', '.list' + i, function (evt) {
                evt.stopPropagation();
                var dom = $(evt.currentTarget);
                var id = dom.data("id");
                var url = dom.data("url");
                console.log('获取数据 >>> ' + id)
                console.log('需要打开的网址 >>> ' + url)
                if (evt.target.className === "delbook") {

                    console.log("要删除的数组下标 >>>" + dom.index())
                    console.log("当前ID >>>" + id);
                    dom.css("overflow", "visible");
                    dom.css({ transform: "translateY(60px)", opacity: 0, transition: ".3s" });
                    dom.on('transitionend', function (evt) {
                        if (evt.target !== this) {
                            return;
                        }
                        dom.remove();
                        that.$ele.css("overflow", "hidden");
                    });
                    // 删除数据
                    const index = findIndexByid(id, data);
                    console.log('下标 >>>> ' + index);
                    data.splice(index, 1);
                    store.set("bookMark", data);
                }
            })
        },

    }



    $.fn.longPress = function (fn) {
        var timeout = void 0,
            $this = this,
            startPos,
            movePos,
            endPos;
        for (var i = $this.length - 1; i > -1; i--) {
            $this[i].addEventListener("touchstart", function (e) {
                var touch = e.targetTouches[0];
                startPos = { x: touch.pageX, y: touch.pageY };
                timeout = setTimeout(function () {
                    if ($this.attr("disabled") === undefined) {
                        fn();
                    }
                }, 700);
            }, { passive: true });
            $this[i].addEventListener("touchmove", function (e) {
                var touch = e.targetTouches[0];
                movePos = { x: touch.pageX - startPos.x, y: touch.pageY - startPos.y };
                (Math.abs(movePos.x) > 10 || Math.abs(movePos.y) > 10) && clearTimeout(timeout);
            }, { passive: true });
            $this[i].addEventListener("touchend", function () {
                clearTimeout(timeout);
            }, { passive: true });
        }
    };

    // 下标查询
    function findIndexByid(id, data) {
        for (let i = 0; i < data.length; i++) {
            if (data[i].id === id) {
                return i;
            }
        }
        return -1;

    }

    // 开始构建
    var bookMark = new bookMarkFn({ data: store.get("bookMark") })
})