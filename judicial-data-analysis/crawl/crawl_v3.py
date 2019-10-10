# coding:utf-8
import socket

socket.setdefaulttimeout(60)
import requests
import urllib2
# import cchardet
import os, time
from lxml import etree
import threading
import re
import random
import sys
reload(sys)
sys.setdefaultencoding('utf-8')


# filenames=os.listdir('.')
# count=0
# for fname in filenames:
# 	if fname.startswith('gid_log'):
# 		count+=1

# gid_path='gid_log_%d' %(count)

# 1、2步分开运行要注意gid_path
# gid_path='gid_log_12'

def get_html(url):  # 得到网页源码
    headers = {
        "Accept-Language": "zh-CN,zh;q=0.8",
        "Accept-Encoding": "gzip, deflate, sdch",
        "Accept": "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8",
        "User-Agent": "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/45.0.2454.101 Safari/537.36",
        "Host": "www.pkulaw.cn",
        "Cookie": "bdyh_record=1970324860086081%2C1970324860087844%2C1970324860087837%2C1970324860087907%2C1970324860085114%2C1970324860087657%2C1970324860087697%2C1970324860087631%2C1970324860087701%2C1970324860087851%2C1970324860086614%2C1970324860000764%2C1970324845231811%2C1970324860004991%2C1970324860002384%2C1970324845231794%2C1970324845231624%2C1970324860002207%2C1970324860046814%2C1970324860046704%2C; CheckIPAuto=0; CheckIPDate=2016-10-15 10:03:46; gm3jc5afyl35gm2yt55kc4m1isIPlogin=1; ASP.NET_SessionId=davttbjhikxhqyn1lj5alhsb; Hm_lvt_58c470ff9657d300e66c7f33590e53a8=1476497011,1476498348,1476498528,1476499578; Hm_lpvt_58c470ff9657d300e66c7f33590e53a8=1476499578; Hm_lvt_8266968662c086f34b2a3e2ae9014bf8=1476497011,1476498348,1476498528,1476499578; Hm_lpvt_8266968662c086f34b2a3e2ae9014bf8=1476499578; CookieId=gm3jc5afyl35gm2yt55kc4m1; FWinCookie=1",
        "Upgrade-Insecure-Requests": "1",
        "Proxy-Connection": "keep-alive"
    }
    html = requests.get(url, headers=headers).text
    return html


def write2file(content, filename):  # 将爬取的文书写入文件保存
    try:
        f = open(filename, 'w')
    except Exception, e:
        filename = filename.split(u'、')[0] + '_error_filename.txt'
        f = open(filename, 'w')
    f.write(content.encode('utf-8'))
    f.close()

    # 下载ihref对应的文书


def load_one_wenshu(gid, title):
    ex_href = 'http://www.pkulaw.cn/case/FullText/_getFulltext?library=pfnl&gid=#gid#&loginSucc=0'
    href = ex_href.replace('#gid#', gid)
    html = get_html(href)
    page = etree.HTML(html)
    content = page.xpath('body')[0].xpath('string(.)').strip()
    write2file(content, filepath + os.sep + title + '.txt')


def load_one_page_wenshu(gid_list, titles):  # 多线程抓取多个href的文书
    # threads=[]   # 尝试多线程加速 失败 访问频繁 出现验证码 封ip
    # for i in range(len(gid_list)):
    # 	gid,title=gid_list[i],titles[i]
    # 	threads.append(threading.Thread(target=load_one_wenshu,args=(gid,title,)))
    # for t in threads:
    # 	t.start()
    # t.join()  # 阻塞

    for i in range(len(gid_list)):  # 顺序爬取 时间过长 一个月大概需要20~30h
        load_one_wenshu(gid_list[i], titles[i])
    # time.sleep(0.1)


# 保存案件标题和id至文件
def save_gids(pageIndex, gid_list, titles, court_list, wenshu_id_list, time_list):
# def save_gids(pageIndex, gid_list, titles, postfix_list):
    fpath = gid_path
    if not os.path.exists(fpath):
        os.mkdir(fpath)
    f = open(fpath + os.sep + str(pageIndex) + '.txt', 'w')
    for i in range(len(gid_list)):
        f.write('%s %s %s %s %s\n' % (titles[i], gid_list[i], court_list[i], wenshu_id_list[i], time_list[i]))
        # f.write('%s %s %s\n' % (titles[i], gid_list[i], postfix_list[i]))
    f.close()


# 得到一页上所有的文书名称和案件id并保存
def get_one_page_all_href(href, pageIndex):
    # print '旧href'+href
    href = href.replace('Pager.PageIndex=0', 'Pager.PageIndex='+str(pageIndex))
    # print '新href'+href
    html = get_html(href)

    # time.sleep(random.random())
    page = etree.HTML(html)
    items = page.xpath('//dl[@class="contentList"]/dd/a')
    items_add = page.xpath('//dl[@class="contentList"]/dd')
    print len(items)
    # 判决书的gid
    gid_list = []
    # title
    titles = []
    # 处理法院
    court_list = []
    # 编号
    wenshu_id_list = []
    # 处理时间
    time_list = []
    # 一个包含“处理法院 编号 处理时间”的字符串数组
    postfix_list = []

    for item in items_add:
        ihref = item[0].attrib['href']
        print ihref
        # title=item.text.strip()
        title = item[0].xpath('string(.)').strip()
        print title
        # if u'、' in title:
        # 	title=title.split(u'、')[1]

        gid = re.findall(r'_(.*?).html', ihref)[0]
        # print "gid: "+gid
        # print len(item[1].xpath('p/span'))
        for i in range(len(item)):
            if item[i].attrib['class'] == 'clearfix':
                innerItem = item[i].xpath('p/span')
                # print len(innerItem)
                # postfix = ""
                court = "null";
                wenshu_id = "null"
                time = "null"
                for j in range(len(innerItem)):
                    # postfix += innerItem[j].xpath('string(.)').strip() + " "
                    if(innerItem[j].attrib['title'] == '审理法院'):
                        court = innerItem[j].xpath('string(.)').strip()
                    if (innerItem[j].attrib['title'] == '案件字号'):
                        wenshu_id = innerItem[j].xpath('string(.)').strip()
                    if (innerItem[j].attrib['title'] == '审结日期'):
                        time = innerItem[j].xpath('string(.)').strip()

                    # print postfix



        # if item[1].attrib['class'] == 'icon':
        #     print "icon"
        #
        # if len(item[1].xpath('p/span')) != 3:
        #     court = "null";
        #     wenshu_id = "null"
        #     time = "null"
        # else:
        #     court = item[1].xpath('string(p/span[1])').strip()
        #     wenshu_id = item[1].xpath('string(p/span[2])').strip()
        #     time = item[1].xpath('string(p/span[3])').strip()
        #
        if gid not in gid_list:
            # print title + " " + gid + " " + wenshu_id + " " + time
            gid_list.append(gid)
            titles.append(title)
            # postfix_list.append(postfix)
            court_list.append(court)
            wenshu_id_list.append(wenshu_id)
            time_list.append(time)


    # print len(set(titles))
    print 'page:%d has %d different case.' % (pageIndex, len(gid_list))
    # load_one_page_wenshu(gid_list,titles)
    save_gids(pageIndex, gid_list, titles, court_list, wenshu_id_list, time_list)
    # save_gids(pageIndex, gid_list, titles, postfix_list)


# 获取当前log文件的所有title和id
def get_titles_gids(filename):
    gid_list = []
    titles = []
    f = open(filename, 'r')
    for line in f:
        pieces = line.strip().split(' ')
        title, gid = pieces[0], pieces[1]
        title = title.replace('?', '')
        # print cchardet.detect(title)
        gid_list.append(gid)
        titles.append(title.decode('utf-8'))
    return gid_list, titles


def load_one_page_from_gid_log(filename):  # 从下载好的gid中开始下载文书
    gid_list, titles = get_titles_gids(filename)  # 得到 gid_list 和 titles
    f = open('href_error_log.txt', 'a')
    for i in range(len(gid_list)):
        try:
            load_one_wenshu(gid_list[i], titles[i])
            print '%s-%d load success..' % (filename, i + 1)
        except Exception, e:  # 若该项抓取出错 记录至error_log.txt
            print '%s-%d load failed...' % (filename, i + 1), e
            f.write('%s-%d:\t%s\t%s\n' % (filename, i + 1, titles[i].encode('utf-8'), gid_list[i]))
            f.flush()
            time.sleep(1)
    f.close()


# 得到目标日期范围内的数据页数pageNum
def getPageNum(href):
    html = get_html(href.replace('#pageIndex#', '0'))
    page = etree.HTML(html)
    pageNum = page.xpath('//*[@id="toppager"]/span/span[2]')
    if pageNum != None:
        pageNum = int(pageNum[0].xpath('string(.)').strip())
    else:
        pageNum = 50
    print 'pageNum:', pageNum
    return pageNum

def main():

    # 强奸罪 法律文书
    # 具体参数所代表的属性参见该网下的请求：http://www.pkulaw.cn/case
    # DocumentAttr 代表文书的种类（001表示判决书、002裁定书、003....）
    # ClassCodeKey 代表何种犯罪类型的文书
    global pageSize
    # 强奸罪判决书大概八千多条：8318(100*83)
    # ClassCodeKey = '0010405%2C%2C' #强奸案
    #ClassCodeKey = '0010502,,'      #盗窃罪 690567条判决书信息
    ClassCodeKey = '0010801,,'
    pageSize = '1000'
    pageNum = 5
    DocumentAttr = '001'
    href = "http://www.pkulaw.cn/case/Search/Record?Menu=CASE&IsFullTextSearch=False&MatchType=Exact&Keywords=&IsAdv=True&AdvTitleMatchType=Exact&AdvFullTextMatchType=0&AdvSearchDic.Title=&AdvSearchDic.FullText=&AdvSearchDic.Category=&AdvSearchDic.CaseFlag=&AdvSearchDic.LastInstanceCourt=&AdvSearchDic.CourtGrade=&AdvSearchDic.Judge=&AdvSearchDic.Agent=&AdvSearchDic.TrialStep=&AdvSearchDic.DocumentAttr="\
           + DocumentAttr +"&AdvSearchDic.TrialStepCount=&AdvSearchDic.LastInstanceDate=&AdvSearchDic.Core=&AdvSearchDic.DisputedIssues=&AdvSearchDic.CaseGist=&AdvSearchDic.CaseGrade=&AdvSearchDic.CriminalPunish=&AdvSearchDic.Accusation=&AdvSearchDic.Criminal=&AdvSearchDic.CivilLaw=&AdvSearchDic.CaseGistMark=&AdvSearchDic.GuidingCase=&AdvSearchDic.GuidingCaseNO=&AdvSearchDic.GuidingCaseDoc=&AdvSearchDic.IssueDate=&AdvSearchDic.ForeignCase=&OrderByIndex=0&GroupByIndex=0&ShowType=1&ClassCodeKey="\
           +ClassCodeKey+"&OrderByIndex=0&GroupByIndex=0&ShowType=1&ClassCodeKey="" \
           "+ClassCodeKey+"&Library=PFNL&SubKeyword=%E5%9C%A8%E7%BB%93%E6%9E%9C%E7%9A%84%E6%A0%87%E9%A2%98%E4%B8%AD%E6%A3%80%E7%B4%A2&GroupIndex=0&GroupValue=&TitleKeywords=&FullTextKeywords=&Pager.PageSize=" \
           + pageSize + "&Pager.PageIndex=0&X-Requested-With=XMLHttpRequest"
    # print href
    global filepath
    filepath='data'


    if not os.path.exists(filepath):
        os.mkdir(filepath)

    global gid_path
    gid_path = filepath + '_log'

    # pageNum = getPageNum(href)  # 得到所有案件页数
    # print pageNum

    # 第一步 下载hrefs 和 titles

    for i in range(pageNum):
        get_one_page_all_href(href, i)

    '''
	# t0=time.time()
	# threads=[]   # 多线程
	# for i in range(459):
	# 	threads.append(threading.Thread(target=get_one_page_all_href,args=(href,i,)))
	# for t in threads:
	# 	t.start()
	# t.join()
	# print 'load %s cost:%.2f' %(filepath,time.time()-t0)
	'''


    # 第二步 根据gid文件下载相应的文书
    for i in range(pageNum):
        f = open('page_error_log.txt', 'a')
        try:
            fname = gid_path + os.sep + str(i) + '.txt'
            load_one_page_from_gid_log(fname)  # 从gid_log中取title和id 下载相关文书
            print '%s load success...' % (fname)
        except Exception, e:
            print '%s load failed...' % (fname), e
            f.write('%s' % (fname))
            f.flush()
            # time.sleep(10)  # 休眠10s
        f.close()


if __name__ == '__main__':
    main()
