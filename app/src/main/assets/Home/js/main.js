$(function () {
    /**
     * 存储获取数据函数
     * @function get 存储数据
     * @function set 获取数据
     */
    const store = {
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

    // // 第一页数据
    // $.ajax({
    //     url: "http://127.0.0.1:8089/api/getHomeData",
    //     type: "get",
    //     dataType: "json",
    //     success: function (res) {
    //         // 合并对象 将本地数据this.options 与 bookMark 对象进行合并
    //         // this.options = $.extend({}, res, options);
    //         console.log(">>>>" + res.data.length)
    //         oneData(res.data);
    //     }
    // })
    //
    // function oneData(oneData) {
    //     console.log("接收到了 >>> " + oneData.length)
    //     // 第一页代码拼接
    //     let oneHtml = '<div style="min-height: 70px;flex: 1;display: flex;flex-wrap:wrap">';
    //     $.each(oneData, function (i, n) {
    //         oneHtml += '<div style="height:100%;flex: 0 0 25%;margin-bottom:5%">' + '<div class="img" style="background-image: url(' + n.img + ')"></div>' + '<div class="text">' + n.hl + '</div></div>';
    //     });
    //     oneHtml += '</div>';
    //     $('#one').append(oneHtml);
    // }


    // 第二页数据
    const bookMarkFn = function (options) {
        // ajax请求 获取书签数据
        $.ajax({
            url: "https://2r242698s2.zicp.fun/api/getBookmarkParent",
            type: "get",
            dataType: "json",
            success: function (res) {
                // 合并对象 将本地数据this.options 与 bookMark 对象进行合并
                this.options = $.extend({}, res, options);
                console.log(">>>>" + this.options)
                this.init();
            }.bind(this)
        })
    };

    bookMarkFn.prototype = {
        // 初始化
        init: function () {
            const html = '';
            const data = this.options.data;
            console.log("获取长度 >>> " + data.length);
            // 将数据总数和每组数据个数存储在变量中
            const totalCount = data.length;
            const firstPageDivCount = 8; // 第一个页面显示的div数量
            const otherPageDivCount = 20; // 其他页面显示的div数量
            // 计算组数并向上取整
            const pageDivCount = totalCount - 8;
            console.log("剩下数量 >>>>" + pageDivCount)
            const numGroups = Math.ceil(pageDivCount / otherPageDivCount);
            console.log("需要的组数是 >>>>" + numGroups);
            // 判断需要创建多少个页面
            let pageHtml;
            let dataHtml;

            // 创建第一个页面
            let oneHtml = '<div style="min-height: 70px;flex: 1;display: flex;flex-wrap:wrap">';
            for (let i = 0; i < firstPageDivCount && i < totalCount; i++) {
                oneHtml += '<div style="height:100%;flex: 0 0 25%;margin-bottom:5%">' + '<div class="img" style="background-image: url(' + data[i].img + ')"></div>' + '<div class="text">' + data[i].hl + '</div></div>';
            }
            oneHtml += '</div>';
            $('#one').append(oneHtml);


            // 其他页面显示的div数量
            for (let i = 1; i <= numGroups; i++) {
                pageHtml = '<div class="swiper-slide">' + '<div style="width: 100%;height: 100%;position: relative;flex-direction: column;text-align: center;" id="page' + i + '"></div></div>'
                console.log("循环次数" + i)
                // 页面内容进行分配
                dataHtml = '<div class="parent" id="one' + i + '">';
                // 使用分页进行显示
                // 一页显示多少个数据
                // 第几页
                const currentPage = i + 1;
                console.log("当前页数 >>> " + currentPage)
                // 进行计算
                const startIndex = firstPageDivCount + (i - 1) * otherPageDivCount;
                const endIndex = startIndex + otherPageDivCount;
                const currentPageData = data.slice(startIndex, endIndex)
                console.log("开始数量 >>> " + startIndex)
                console.log("结束数量 >>> " + endIndex)
                console.log("当前页数数据 >>> " + currentPageData.length)
                // 计时器
                let timerId;

                for (let x = 0; x < currentPageData.length; x++) {
                    // 分页循环
                    console.log("APP >>> " + currentPageData[x].id);
                    console.log("APP >>> " + currentPageData[x].url);
                    const id = currentPageData[x].id;
                    const url = currentPageData[x].url;
                    dataHtml += '<div class="child list' + i + '" id="app' + id + '"' + ' data-id="' + id + '" data-url="' + url + '" style="height:100%;margin-bottom:5%">' + '<div style=""><div class="img" style="background-image: url(' + currentPageData[x].img + ')"></div><div class="text">' + currentPageData[x].hl + '</div></div></div>';


                    // $(document).click(function () {
                    //     console.log('触发了')
                    //     $(".delbook").removeClass();
                    // });


                    //长按事件
                    // $(document).on('touchstart', '#app' + currentPageData[x].id, function (e) {
                    //     var elementId = $(this).attr('id'); // 获取控件的 ID
                    //     var data_id = $(this).attr('data_id'); // 获取数据ID
                    //     // 开始长按的代码delbook
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
                this.$ele = $('#page' + i);
                console.log("输出 #page >>>> " + i)
                console.log("输出 >>>> " + dataHtml)
                // 显示页面
                this.$ele.html(dataHtml);

                console.log()
                // 控件綁定
                this.bind(i);
            }


            // 页面滑动
            const swiper = new Swiper('.swiper-container', {

                pagination: {
                    el: ".swiper-pagination",
                    dynamicBullets: true,
                }

                // direction: 'horizontal',
                // spaceBetween: 0,
                // loop: false,
                // pagination: {
                //     el: '.swiper-pagination',
                //     clickable: true,
                // },
                // touchEventsTarget: 'wrapper',
                // preventInteractionOnTransition: true,
            });

        },

        bind: function (i) {
            console.log('获取当前类 >>> ' + i)
            const that = this;
            const data = this.options.data;
            // 计时器
            let timeout;
            let id = 'one' + i


            // 长按事件
            // this.$ele.longPress(function (evt) {
            //     evt.stopPropagation();
            //     var dom = $(evt.currentTarget);
            //     var id = dom.data("id");
            //     // 删除数据
            //     const index = findIndexByid(id, data);
            //     console.log('下标 >>>> ' + index);
            //     openModal(data, index)
            // });

            //长按事件
            this.$ele.on('touchstart', '.list' + i, function (evt) {
                // 开始长按的代码
                timeout = setTimeout(function () {
                    const dom = $(evt.currentTarget);
                    const id = dom.data("id");
                    const index = findIndexByid(id, data);
                    // openModal(dom, evt, store, data, index)
                }, 700);
            }).on('touchend', '.list' + i, function (e) {
                clearTimeout(timeout);
            });


            // let container = document.querySelector('.swiper-container');

            // container.addEventListener('touchend', (event) => {
            //     let touch = event.changedTouches[0];
            //     if (touch.pageX > window.innerWidth * 0.9) {
            //         swiper.slideNext();
            //     } else if (touch.pageX < window.innerWidth * 0.1) {
            //         swiper.slidePrev();
            //     }
            // });


            const sortable = new Sortable(document.getElementById(id), {
                group: 'shared', // 设置为相同的值，以便两个列表能相互拖动
                animation: 150,
                // onMove: function (){
                //     container.addEventListener('touchend', (event) => {
                //         let touch = event.changedTouches[0];
                //         if (touch.pageX > window.innerWidth * 0.9) {
                //             swiper.slideNext();
                //         } else if (touch.pageX < window.innerWidth * 0.1) {
                //             swiper.slidePrev();
                //         }
                //     });
                // }
            });


            sortable.option('onMove', function (evt, originalEvent) {
                let touch;
                const windowWidth = window.innerWidth;
                const threshold = 50;  // 定义页面边缘的阈值，这可以根据需要进行调整

                // 检查 originalEvent 是鼠标事件还是触摸事件
                if (originalEvent.touches) {
                    touch = originalEvent.touches[0];
                } else {
                    touch = originalEvent;
                }

                if (touch.clientX > windowWidth - threshold) {
                    swiper.slideNext();
                } else if (touch.clientX < threshold) {
                    swiper.slidePrev();
                }
            });


            // 点击事件
            this.$ele.on('click', '.list' + i, function (evt) {
                evt.stopPropagation();
                const dom = $(evt.currentTarget);
                const id = dom.data("id");
                const url = dom.data("url");
                console.log('获取数据 >>> ' + id)
                console.log('需要打开的网址 >>> ' + url)
                // 根据Ajax进行查询判断是否存在
                $.ajax({
                    url: "https://2r242698s2.zicp.fun/api/getBookmarkChild?bookId=" + id,
                    type: "get",
                    dataType: "json",
                    success: function (res) {
                        console.log(">>>>" + res.data)
                        openPage(res, i);
                    },
                    error: function (res) {
                        console.log("错误 >>> " + res)
                    }
                });

                // switch (url) {
                //     case "直播":
                //         openPage("直播", i);
                //         break;
                //
                //     default:
                //         location.href = url
                // }
                //
                // if (evt.target.className === "delbook") {
                //
                //     console.log("要删除的数组下标 >>>" + dom.index())
                //     console.log("当前ID >>>" + id);
                //     dom.css("overflow", "visible");
                //     dom.css({transform: "translateY(60px)", opacity: 0, transition: ".3s"});
                //     dom.on('transitionend', function (evt) {
                //         if (evt.target !== this) {
                //             return;
                //         }
                //         dom.remove();
                //         that.$ele.css("overflow", "hidden");
                //     });
                //     // 删除数据
                //     const index = findIndexByid(id, data);
                //     console.log('下标 >>>> ' + index);
                //     data.splice(index, 1);
                //     store.set("bookMark", data);
                // }
            })
        },

    }

    // 图标点击事件
    function openPage(res, i) {
        // 插入html
        $('#one' + i).append(`<div class="page-bg">
                <div class="page-addbook">
                    <div class="addbook-content">
                        <div class="page bookmark twoParent" style="text-align: center;margin: 5%;overflow: auto;">
                        </div>
                    </div>
                </div></div>`);

        // 进行数据绑定
        dataBind(res, i);

        setTimeout(function () {
            $(".page-bg").addClass("animation");

            $(".addbook-content").addClass("animation");
        }, 50);

        //绑定事件
        $("#addbook-upload").click(function () {
            openFile(function () {
                var file = this.files[0];
                var reader = new FileReader();
                reader.onload = function () {
                    $("#addbook-upload").html('<img src="' + this.result + '"></img><p>' + file.name + '</p>');
                };
                $("#addbook-upload").css("pointer-events", "");
                $(".addbook-ok").css("pointer-events", "");
                reader.readAsDataURL(file);
            });
        });


        $(".addbook-ok").click(function () {
            let name = $(".addbook-name").val(),
                url = $(".addbook-url").val(),
                icon = $("#addbook-upload img").attr("src");
            if (name.length && url.length) {
                if (!icon) {
                    // 绘制文字图标
                    const canvas = document.createElement("canvas");
                    canvas.height = 100;
                    canvas.width = 100;
                    const ctx = canvas.getContext("2d");
                    ctx.fillStyle = "#f5f5f5";
                    ctx.fillRect(0, 0, 100, 100);
                    ctx.fill();
                    ctx.fillStyle = "#222";
                    ctx.font = "40px Arial";
                    ctx.textAlign = "center";
                    ctx.textBaseline = "middle";
                    ctx.fillText(name.substr(0, 1), 50, 52);
                    icon = canvas.toDataURL("image/png");
                }
                bookMark.add(name, url, icon);
            }
        });

        // 取消事件
        $(".page-bg").click(function () {
            $(".page-addbook").css({"pointer-events": "none"});
            $(".page-bg").removeClass("animation");

            $(".addbook-content").removeClass("animation");
            setTimeout(function () {
                $(".page-addbook").remove();
                $(".page-bg").remove();
            }, 300);
        });

        $(".page-addbook").click(function (evt) {
            if (evt.target === evt.currentTarget) {
                $(".bottom-close").click();
            }
        });


    }

    // 子书签数据
    function dataBind(res) {
        console.log("子书签数据 >>> " + res.data.length)

        const bookMarkFn = function (ele, options) {
            this.$ele = $(ele);
            this.options = $.extend({}, res, options);
            this.init();
        };

        bookMarkFn.prototype = {
            init: function () {
                console.log("初始化 >>>" + this.options.data.length)
                let html = '';
                const data = this.options.data;
                let i = 0, l = data.length;
                for (; i < l; i++) {
                    console.log("打印")
                    html += '<div class="twoChild" onclick="onClick(this)" name = "' + data[i].url + '">' +
                        '<div style="">' +
                        // '<div class="img" style="background-image:url(' + data[i].icon + ')"></div>' +
                        '<div class="text">' + data[i].hl + "</div></div></div>";
                }
                this.$ele.html(html);
            },
            getJson: function () {
                return this.options.data;
            },
        }
        console.log('这个是什么 >>>>>')
        // 开始构建
        const bookMark = new bookMarkFn($('.page'), {data: store.get("page")});
    }

    // 影视书签


    /**
     * 文件打开函数
     * @param callback 回调函数
     */
    var openFile = function (callback) {
        $('.openFile').remove();
        var input = $('<input class="openFile" type="file">');
        input.on("propertychange change", callback);
        $('body').append(input);
        input.click();
    }


    // 生成二级页面图标
    var getRandomColor = function () {

        return '#' +
            (function (color) {
                return (color += '0123456789abcdef'[Math.floor(Math.random() * 16)])
                && (color.length === 6) ? color : arguments.callee(color);
            })('');
    }


    $.fn.longPress = function (fn) {
        let timeout = void 0,
            $this = this,
            startPos,
            movePos,
            endPos;
        for (let i = $this.length - 1; i > -1; i--) {
            $this[i].addEventListener("touchstart", function (e) {
                const touch = e.targetTouches[0];
                startPos = {x: touch.pageX, y: touch.pageY};
                timeout = setTimeout(function () {
                    if ($this.attr("disabled") === undefined) {
                        fn();
                    }
                }, 700);
            }, {passive: true});
            $this[i].addEventListener("touchmove", function (e) {
                const touch = e.targetTouches[0];
                movePos = {x: touch.pageX - startPos.x, y: touch.pageY - startPos.y};
                (Math.abs(movePos.x) > 10 || Math.abs(movePos.y) > 10) && clearTimeout(timeout);
            }, {passive: true});
            $this[i].addEventListener("touchend", function () {
                clearTimeout(timeout);
            }, {passive: true});
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
    const bookMark = new bookMarkFn({data: store.get("page")});


})
