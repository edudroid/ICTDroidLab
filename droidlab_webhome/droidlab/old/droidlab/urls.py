from django.conf.urls import patterns, include, url

from django.contrib import admin

admin.autodiscover()

urlpatterns = patterns('',
    # Examples:
    # url(r'^$', 'droidlab.views.home', name='home'),
    # url(r'^blog/', include('blog.urls')),

    url(r'^admin/', include(admin.site.urls)),
    url(r'^$', 'droidlab.views.index', name='index'),
    url(r'^crowdsensing/', 'droidlab.views.crowdsensing', name='crowdsensing'),
    url(r'^details/', 'droidlab.views.details', name='details'),
    url(r'^team/', 'droidlab.views.team', name='team'),
    url(r'^downloads/', 'droidlab.views.downloads', name='downloads'),
    url(r'^publications/', 'droidlab.views.publications', name='publications'),
    url(r'^news/', 'droidlab.views.news', name='news'),
    url(r'^post/', 'droidlab.views.post', name='post'),
)