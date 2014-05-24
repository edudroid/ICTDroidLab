from django.template import loader, RequestContext
from django.http.response import HttpResponse

def index(request):
    context = RequestContext(request)
    header = loader.get_template('droidlab/header.html').render(context)
    footer = loader.get_template('droidlab/footer.html').render(context)
    body = loader.get_template('droidlab/index.html').render(context)
    return HttpResponse(header + body + footer)

def details(request):
    context = RequestContext(request)
    header = loader.get_template('droidlab/header.html').render(context)
    footer = loader.get_template('droidlab/footer.html').render(context)
    body = loader.get_template('droidlab/details.html').render(context)
    return HttpResponse(header + body + footer)


def team(request):
    context = RequestContext(request)
    header = loader.get_template('droidlab/header.html').render(context)
    footer = loader.get_template('droidlab/footer.html').render(context)
    body = loader.get_template('droidlab/team.html').render(context)
    return HttpResponse(header + body + footer)

def downloads(request):
    context = RequestContext(request)
    header = loader.get_template('droidlab/header.html').render(context)
    footer = loader.get_template('droidlab/footer.html').render(context)
    body = loader.get_template('droidlab/downloads.html').render(context)
    return HttpResponse(header + body + footer)

def publications(request):
    context = RequestContext(request)
    header = loader.get_template('droidlab/header.html').render(context)
    footer = loader.get_template('droidlab/footer.html').render(context)
    body = loader.get_template('droidlab/publications.html').render(context)
    return HttpResponse(header + body + footer)

def news(request):
    context = RequestContext(request)
    header = loader.get_template('droidlab/header.html').render(context)
    footer = loader.get_template('droidlab/footer.html').render(context)
    body = loader.get_template('droidlab/news.html').render(context)
    return HttpResponse(header + body + footer)

def post(request):
    context = RequestContext(request)
    header = loader.get_template('droidlab/header.html').render(context)
    footer = loader.get_template('droidlab/footer.html').render(context)
    body = loader.get_template('droidlab/post.html').render(context)
    return HttpResponse(header + body + footer)

